#!/bin/bash -ex

#
# Each step is idempotent by creating a 'step-#.done' file after
# successfully executing.
#

JENKINS_KEY=/jenkins/.ssh/id_rsa
GERRIT_KEY=/init/id_rsa-workshop
SSH_OPTIONS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
CI_MANAGEMENT_REPO=/init/ci-management
JJB_VERSION=${JJB_VERSION:-2.0.3}

##
# Jenkins Setup
##
/docker-entrypoint-init.d/wait-for-it.sh jenkins:8080 -t 30

# Generate a key for the jenkins user
if [ ! -f /init/ssh-key-jenkins.done ]; then
mkdir -p /jenkins/.ssh/
ssh-keygen -t rsa -N '' -f $JENKINS_KEY
chown -R 1000:1000 /jenkins/.ssh/
touch /init/ssh-key-jenkins.done
fi

##
# Gerrit Setup
##
./docker-entrypoint-init.d/wait-for-it.sh gerrit:29418 -t 90

# Create Jenkins ssh user in Gerrit
if [ ! -f /init/jenkins-step-1.done ]; then
ssh $SSH_OPTIONS -p 29418 workshop@gerrit -i $GERRIT_KEY \
    gerrit create-account jenkins-workshop --full-name "Jenkins\ Workshop" \
    --group "Non-Interactive\ Users" --ssh-key - < "$JENKINS_KEY.pub" \
    && touch /init/jenkins-step-1.done
fi

# Populate ci-management repository with global-jjb
if [ ! -f /init/jenkins-step-2.done ]; then
    eval "$(ssh-agent)"
    ssh-add $GERRIT_KEY
    mkdir -p $CI_MANAGEMENT_REPO/jjb
    cd $CI_MANAGEMENT_REPO/jjb
    git submodule add https://github.com/lfit/releng-global-jjb global-jjb
    cd $CI_MANAGEMENT_REPO/jjb/global-jjb
    git checkout $GLOBAL_JJB_VERSION
    cd $CI_MANAGEMENT_REPO
    git add jjb/global-jjb
    git commit -am "Install global-jjb $GLOBAL_JJB_VERSION"
    git push origin HEAD:refs/heads/master
    touch /init/jenkins-step-2.done
fi

# Populate ci-management with defaults
if [ ! -f /init/jenkins-step-3.done ]; then
    cd $CI_MANAGEMENT_REPO
    cat > $CI_MANAGEMENT_REPO/jjb/ci-management.yaml <<-EOF
---
- project:
    name: ci-jobs

    jobs:
      - '{project-name}-ci-jobs'

    project: ci-management
    project-name: ci-management
    build-node: ciworkshop
EOF

    cat > $CI_MANAGEMENT_REPO/jjb/defaults.yaml <<-EOF
---
- defaults:
    name: global

    # lf-infra defaults
    jenkins-ssh-credential: ciworkshop-jenkins-ssh
    gerrit-server-name: ciworkshop
    lftools-version: '<1.0.0'
EOF
    git add .
    git commit -am "Initial JJB Files"
    git push origin HEAD:refs/heads/master
    touch /init/jenkins-step-3.done
fi

#  Upload Jenkins Jobs
if [ ! -f /init/jenkins-step-4.done ]; then
    cd $CI_MANAGEMENT_REPO
    pip install --upgrade "pip<10.0.0" setuptools wheel
    pip install "jenkins-job-builder==$JJB_VERSION"
    cat > $CI_MANAGEMENT_REPO/jenkins.ini <<-EOF
[job_builder]
ignore_cache=True
keep_descriptions=False
include_path=.:scripts:~/git/
recursive=True

[jenkins]
url=http://jenkins:8080/
user=workshop
password=workshop
query_plugins_info=True
EOF
    # Ensure JJB is installed first
    jenkins-jobs --conf jenkins.ini update -r jjb/
    touch /init/jenkins-step-4.done
fi
