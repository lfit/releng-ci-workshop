Linux Foundation Release Engineering Workshop
=============================================

This is a container environment containing services used by the Linux
Foundation Release Engineering team. It is loosely influenced by the work
done by openfrontier_ (author of the Gerrit container).

The goal of this project is to provide our Release Engineering team a
simple way to bring up an environment representative of our production
CI environment where we can work on proof-of-concepts, test
configuration, or compare differences between service versions.

Here are a few examples of things this project can be used for:

 * Verifing and testing upgrades to Jenkins and Gerrit plugins and
   releases.
 * Testing Jenkins and Gerrit-Trigger integration
 * Nexus configuration
 * Gerrit/LDAP group integration

The primary services are:

 * Gerrit
 * Nexus
 * OpenLDAP
 * PostgreSQL
 * NGINX

These are supplemented with Jenkins by default or optionally by Zuul.

Most of the documentation for individual services and how they are
configured can be found in the `config.env` file. This file defines most
of the environment variable passed to the containers.

Quick Reference
---------------


Add the following to /etc/hosts::

  127.0.1.1 jenkins.localhost
  127.0.1.2 gerrit.localhost
  127.0.1.3 nexus.localhost

Jenkins/Gerrit login: workshop/workshop
Nexus login: admin/admin123

Getting Started
---------------

Add the following to /etc/hosts::

  127.0.1.1 jenkins.localhost
  127.0.1.2 gerrit.localhost
  127.0.1.3 nexus.localhost

.. Note: This is the same as setting the 'Host' header when sending a GET
   request to localhost: `curl -H "Host: gerrit.localhost" localhost`

Jenkins
~~~~~~~

.. code-block::

  docker-compose up -d

Will bring up an environment containing all the services with
authentication backed by LDAP, a simple ci-management repo in
Gerrit, and a basic job in Jenkins that verifies commits to the
ci-management repo.

Zuul
~~~~

.. code-block::

  docker-compose -f docker-compose.yml -f docker-compose.override-zuul.yml up -d

Once the environment is up and running, copy your ssh public-key and add
it to the workshop user in Gerrit. This can be either be done through the
web interface or from the commandline::

  ./gerrit-auth.sh ~/.ssh/id_rsa.pub

Then you can clone the ci-management repo and modify it to your hearts
content::

  git clone ssh://workshop@gerrit.localhost:29418/ci-management.git

Set the gitreview username::

  git config --add gitreview.username "workshop"

And ensure the Change-Id hook exists::

  git review -s

Putting up a patchset for review that modifies "\*.yaml" files should
trigger the ci-management-jjb-verify job and add a -1/+1 Verified vote.

Notes
-----

To bring up a single service in the foreground you can use:

.. code-block::

  docker-compose up $SERVICE

Note: dependent services will still be launched but in the background.
Hitting '^C' will stop this service, but not the others.

If a service is backed by a Dockerfile, then changes to the Dockerfile
or files under '$SERVICE/' will require rebuilding the container:

.. code-block::

  docker-compose build

To tear down the environment removing all the volumes, and start from
scratch, run:

.. code-block::

  docker-compose down -v

To run a specific version of one of the services, edit the `.env` file,
and rebuild the containers. For example, to run Jenkins 2.80 set the
value in the `.env` file::

  JENKINS_CONTAINER_VERSION=2.80

and run::

  docker-compose up -d --build

to rebuild the Jenkins image before launching it.

For other useful docker-compose commands such as logs, see::

  docker-compose -h

Init Container
~~~~~~~~~~~~~~

In order to fully configure both Jenkins and Gerrit, another container
'init' is added as part of the startup to generate ssh keys, create the
ci-management repo, configure users, and push the ci-management jobs to
Jenkins.

This is done in a weakly idempotent fashion by creating files after the
command execute successfuly, so that if the environment is restarted the
container doesn't die or modify existing data.


TODO
~~~~

The following is a list of automation tasks still needed before the
environment can be considered stable:

General:
- [x] Replace 'sandbox' names with 'workshop' since sandbox was just a
      placeholder
- [ ] Setup OpenLDAP over SSL by default
- [ ] Make things more configurable. There are a lot of hardcoded names
      in Groovy scripts which could be pulled from environment variables
- [x] Collapse environment config into single file and add lots of
      comments, so users don't need to track down the correct file

Nexus:
- [ ] Configure Nexus to use LDAP (admin/admin123, or LDAP)
- [x] Setup and configure Nexus
  - [x] Create 'logs' Nexus site repo.

Gerrit:
- [ ] Remove postgres container configuration and replace with MariaDB
  (or make optional)

Jenkins:
- [ ] Fix (on Jenkins restart)::
      WARNING: Caught exception evaluating:
      instance.hasExplicitPermission(attrs.sid,p) in /configureSecurity/.
      Reason: java.lang.NullPointerException
- [ ] Make Groovy scripts Idempotent
- [x] Set Markup Formatter to HTML Output
- [x] Add LOGS_SERVER, SILO, NEXUS_URL, JENKINS_HOSTNAME
- [x] Create XML config file 'jenkins-log-archives-settings' (depends on credentials)
- [x] Install environment injector plugin
      https://wiki.jenkins.io/display/JENKINS/EnvInject+Plugin
- [x] Install plugin for build description
      https://plugins.jenkins.io/description-setter
- [x] Manually install postbuildscript.hpi
      http://mirrors.jenkins-ci.org/plugins/postbuildscript/0.17/postbuildscript.hpi

Init:
- [ ] Make steps strongly idempotent (verify the state they modify)

.. _environment: https://docs.docker.com/compose/environment-variables/#configuring-compose-using-environment-variables
.. _variables: https://docs.docker.com/samples/nginx/#using-environment-variables-in-nginx-configuration
.. _openfrontier: https://github.com/openfrontier/ci-compose
.. _jwilder/nginx-proxy: https://github.com/jwilder/nginx-proxy
