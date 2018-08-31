#!/bin/bash -ex

#
# Each step is idempotent by creating a 'step-#.done' file after
# successfully executing.
#

# CI_SYSTEM=${CI_SYSTEM:jenkins}
GERRIT_KEY=/init/id_rsa-workshop
SSH_OPTIONS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
CI_MANAGEMENT_REPO=/init/ci-management
GLOBAL_JJB_VERSION=${GLOBAL_JJB_VERSION:-v0.19.2}

# Generate a key for the workshop user
if [ ! -f /init/ssh-key-workshop.done ]; then
ssh-keygen -t rsa -N '' -f $GERRIT_KEY
touch /init/ssh-key-workshop.done
fi

##
# Gerrit Login
##
/docker-entrypoint-init.d/wait-for-it.sh gerrit:8080 -t 90

# Be the first to login to gain Administrative rights
if [ ! -f /init/step-1.done ]; then
curl -X POST --data "username=workshop&password=workshop" http://gerrit:8080/login \
    && touch /init/step-1.done
fi

##
# Gerrit Setup
##
./wait-for-it.sh gerrit:29418 -t 90

# Add generated ssh-pubkey to Gerrit keypairs
if [ ! -f /init/step-2.done ]; then
curl -X POST --user "workshop:workshop" -H "Content-type: plain/text" \
    --data @"$GERRIT_KEY.pub" "http://gerrit:8080/a/accounts/self/sshkeys" \
    && touch /init/step-2.done
fi

# Create ci-management repository
if [ ! -f /init/step-3.done ]; then
ssh $SSH_OPTIONS -p 29418 workshop@gerrit -i $GERRIT_KEY \
    gerrit create-project ci-management --id --so --empty-commit \
    -d "Workshop\ CI-Management\ Repo" -p "All-Projects" \
    && touch /init/step-3.done
fi

# Configure Git user and grab Gerrit hostkey
if [ ! -f /init/step-4.done ]; then
    ssh-keyscan -p 29418 gerrit >> /etc/ssh/ssh_known_hosts
    git config --file ~/.gitconfig user.email "workshop@example.org"
    git config --file ~/.gitconfig user.name "workshop"
    touch /init/step-4.done
fi

# Populate ci-management with defaults
if [ ! -f /init/step-5.done ]; then
    eval "$(ssh-agent)"
    ssh-add $GERRIT_KEY
    git clone ssh://workshop@gerrit:29418/ci-management.git $CI_MANAGEMENT_REPO
    cd $CI_MANAGEMENT_REPO
    cat > $CI_MANAGEMENT_REPO/.gitreview <<-EOF
[gerrit]
host=gerrit.localhost
port=29418
username=workshop
project=ci-management.git
defaultbranch=master
EOF
    git add .
    git commit -am "Initial Commit"
    git push origin HEAD:refs/heads/master
    touch /init/step-5.done
fi

# Add Verified Label
if [ ! -f /init/step-6.done ]; then
    eval "$(ssh-agent)"
    ssh-add $GERRIT_KEY

    ALL_PROJECTS=/tmp/All-Projects
    mkdir -p /tmp/All-Projects

    cd $ALL_PROJECTS
    git init
    git remote add origin ssh://workshop@gerrit:29418/All-Projects.git
    git fetch origin refs/meta/config:refs/remotes/origin/meta/config
    git checkout meta/config

    git config -f project.config label.Verified.function MaxWithBlock
    git config -f project.config --add label.Verified.defaultValue 0
    git config -f project.config --add label.Verified.value "-1 Fails"
    git config -f project.config --add label.Verified.value "0 No score"
    git config -f project.config --add label.Verified.value "+1 Verified"
    git config -f project.config --add access.refs/heads/*.label-Verified "-1..+1 group Non-Interactive Users"

    git commit -am "Create Verified Label"
    git push origin meta/config:meta/config

    touch /init/step-6.done
fi

##
# Nexus Setup
##
/docker-entrypoint-init.d/wait-for-it.sh nexus:8081 -t 30


# Create Nexus Repos
if [ ! -f /init/step-7.done ]; then
    cat > /init/repo.json <<-EOF
{
  "data": {
    "name": "logs",
    "repoType": "hosted",
    "providerRole": "org.sonatype.nexus.proxy.repository.WebSiteRepository",
    "exposed": true,
    "id": "logs",
    "provider": "site",
    "writePolicy": "ALLOW_WRITE",
    "browseable": true,
    "indexable": true,
    "notFoundCacheTTL": 1440,
    "repoPolicy": "MIXED"
  }
}
EOF
    curl -H "Content-Type: application/json" -X POST -d @/init/repo.json \
      -u admin:admin123 http://nexus:8081/nexus/service/local/repositories

    touch /init/step-7.done
fi

/docker-entrypoint-init.d/config-${CI_SYSTEM}-environment.sh
