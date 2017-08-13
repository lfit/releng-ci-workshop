Linux Foundation Release Engineering Workshop
=============================================

This is a container environment containing services used by the Linux
Foundation Release Engineering team. It is loosely influenced by the work
done by openfrontier_ (author of the Gerrit container).

The primary services are:

 * Gerrit
 * Jenkins
 * Nexus [TODO]

And the secondary services that support these:

 * OpenLDAP
 * NGINX
 * MariaDB [TODO]
 * HAProxy [TODO]

Most of the documentation for individual services and how they are
configured can be found under the `config/` directory, and
`$SERVICE.env` files contain environment_ variables used by the docker
containers.

Quick Reference
---------------

Add the following to /etc/hosts::

  127.0.1.1 jenkins
  127.0.1.2 gerrit

Default user account: sandbox/sandbox

Getting Started
---------------

Add the following to /etc/hosts::

  127.0.1.1 jenkins
  127.0.1.2 gerrit

.. Note: This is the same as setting the 'Host' header when sending a GET
   request to localhost: `curl -H "Host: gerrit" localhost`

.. code-block::

  docker-compose up -d

Will bring up an environment containing all the services with
authentication backed by LDAP, a simple ci-management repo in
Gerrit, and a basic job in Jenkins that verifies commits to the
ci-management repo.

To bring up a single service in the foreground you can use:

.. code-block::

  docker-compose up $SERVICE

Note: dependent services will still be launched but in the background.
Hitting '^C' will stop this service, but not the others.

If a service is backed by a Dockerfile (most will be eventually), then
changes to the Dockerfile or files under '$SERVICE/' will require
rebuilding the container:

.. code-block::

  docker-compose build

To tear down the environment removing all the volumes, and start from
scratch, run:

.. code-block::

  docker-compose down -v

For other useful docker-compose commands such as logs, see::

  docker-compose -h

Next Steps
----------

Once the environment is up and running, copy your ssh public-key and add
it to the sandbox user in Gerrit. This can be either be done through the
web interface or from the commandline::

  curl -L -X POST -u "sandbox:sandbox" -H "Content-type:text/plain" \
    -d @$HOME/.ssh/id_rsa.pub http://gerrit/a/accounts/self/sshkeys/

.. note: It's important here the Content-type header is set, as Gerrit
   always expects JSON, and URLs must end in '/'

Then you can clone the ci-management repo and modify it to your hearts
content::

  git clone ssh://sandbox@gerrit:29418/ci-management.git

Set the gitreview username::

  git config --add gitreview.username "sandbox"

And ensure the Change-Id hook exists::

  git review -s

Putting up a patchset for review that modifies "\*.yaml" files should
trigger the ci-management-jjb-verify job and add a -1/+1 Verified vote.

Notes
-----

Init Container
~~~~~~~~~~~~~~

In order to fully configure both Jenkins and Gerrit, another container
'init' is added as part of the startup to generate ssh keys, create the
ci-management repo, configure users, and push the ci-management jobs to
Jenkins.

This is done in a weakly idempotent fashion by creating files after the
command execute successfuly, so that if the environment is restarted the
container doesn't die or modify existing data.

Goals
~~~~~

The goal of this project is to have an easily created workshop where
releng work can be tested or proof-of-concepts created.

Some examples:

 * Jenkins Plugin upgrades
 * Gerrit upgrades
 * Jenkins and Gerrit-Trigger testing
 * Nexus configuration
 * Gerrit/LDAP group integration

TODO
~~~~

The following is a list of automation tasks still needed before the
environment can be considered stable:

General:
- [ ] Replace 'sandbox' names with 'workshop' since sandbox was just a
      placeholder
- [ ] Setup OpenLDAP over SSL by default
- [ ] Collapse environment config into single file and add lots of
      comments, so users don't need to track down the correct file
- [ ] Make things more configurable. There are a lot of hardcoded names
      in Groovy scripts which could be pulled from environment variables

Nexus:
- [ ] Setup and configure Nexus

Gerrit:
- [ ] Remove postgres container configuration and replace with MariaDB
  (or make optional)

Jenkins:
- [ ] Fix (on Jenkins restart)::
      WARNING: Caught exception evaluating:
      instance.hasExplicitPermission(attrs.sid,p) in /configureSecurity/.
      Reason: java.lang.NullPointerException
- [ ] Make Groovy scripts Idempotent

Init:
- [ ] Make steps strongly idempotent (verify the state they modify)

.. _environment: https://docs.docker.com/compose/environment-variables/#configuring-compose-using-environment-variables
.. _variables: https://docs.docker.com/samples/nginx/#using-environment-variables-in-nginx-configuration
.. _openfrontier: https://github.com/openfrontier/ci-compose
.. _jwilder/nginx-proxy: https://github.com/jwilder/nginx-proxy
