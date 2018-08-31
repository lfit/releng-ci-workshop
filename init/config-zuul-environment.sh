#!/bin/bash -ex

#
# Each step is idempotent by creating a 'step-#.done' file after
# successfully executing.
#

ZUUL_KEY=/zuul/.ssh/id_rsa
GERRIT_KEY=/init/id_rsa-workshop
SSH_OPTIONS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"
# CI_MANAGEMENT_REPO=/init/ci-management

# Generate a key for the zuul user
if [ ! -f /init/ssh-key-zuul.done ]; then
mkdir -p /zuul/.ssh/
ssh-keygen -t rsa -N '' -f $ZUUL_KEY
chown -R 1000:1000 /zuul/.ssh/
touch /init/ssh-key-zuul.done
fi

##
# Gerrit Setup
##
/docker-entrypoint-init.d/wait-for-it.sh gerrit:29418 -t 90


# Create Zuul ssh user in Gerrit
if [ ! -f /init/zuul-step-1.done ]; then
ssh $SSH_OPTIONS -p 29418 workshop@gerrit -i $GERRIT_KEY \
    gerrit create-account zuul --full-name "Zuul" \
    --group "Non-Interactive\ Users" --ssh-key - < "$ZUUL_KEY.pub"
ssh $SSH_OPTIONS -p 29418 workshop@gerrit -i $GERRIT_KEY \
    gerrit flush-caches --all \
    && touch /init/zuul-step-1.done
fi
