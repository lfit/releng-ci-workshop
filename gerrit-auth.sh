#!/bin/bash

KEYFILE=$1

function usage() {
    # Print usage and exit
    cat <<EOF
Usage: $0 <public-key-file>

$0 uploads an ssh public key to Gerrit, so repos can be
accessed from ssh://gerrit.localhost:29418.

Example:
  $0 ~/.ssh/id_rsa.pub
EOF
exit 1
}

function print_repos() {
    # Print out a list of array elements
    local repos=("$@")
    for repo in "${repos[@]}"; do
        echo -e " - $repo"
    done
}

if [ -z "$*" ]; then
    usage
fi

if [[ -s $KEYFILE ]]; then
    # Upload SSH Public Key
    curl --fail -s -L -X POST -u "sandbox:sandbox" -H "Content-type:text/plain" \
      -d "@$KEYFILE" http://gerrit.localhost/a/accounts/self/sshkeys/ > /dev/null

    # Provide guidance on curl errors
    if [ $? -eq 7 ]; then
        echo -e "\nPlease start Gerrit first:\n  docker-compose up -d"
    elif [ $? -eq 22 ]; then
        echo -e "\nPlease wait for Gerrit to finish running and try again"
    fi

    # Output future guidance
    if [ $? -eq 0 ]; then
        KEYID=$(ssh-keygen -l -f "$KEYFILE")
        GERRIT_REPOS="$(curl -s -L http://gerrit.localhost/projects/ \
            | grep \"id\" | cut -c12- | tr -d '",')"
        echo -e "Successfully uploaded public keyfile:"
        echo "  $KEYID"
        echo -e "\nYou can now clone the available repos:"
        print_repos $GERRIT_REPOS
        echo -e "\nWith the command:"
        echo -e "  git clone ssh://sandbox@gerrit.localhost:29418/<repo>"
    fi
fi
