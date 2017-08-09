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

Jenkins
  localhost:8001

Gerrit
  localhost:8000
  localhost:29418

Default user account: sandbox/sandbox

Getting Started
---------------

.. code-block::

  docker-compose up -d
  
Will bring up an environment containing all the services with
authentication backed by LDAP, a simple ci-management repo in
Gerrit[TODO], and a basic job in Jenkins that verifies commits to the
ci-management repo[TODO].

To bring up a single service in the foreground you can use[TODO - Nginx]:

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

Default User
------------

The default username and password in LDAP are:

 user: sandbox
 pass: sandbox

This user is part of the 'sandbox-admins' group and should have full
admin rights on all services.

Goals
-----

The goal of this project is to have an easily created workshop where
releng work can be tested or proof-of-concepts created.

Some examples:

 * Jenkins Plugin upgrades
 * Gerrit upgrades
 * Jenkins and Gerrit-Trigger testing
 * Nexus configuration
 * Gerrit/LDAP group integration

TODO
----

The following is a list of automation tasks still needed before the
environment can be considered usable:

Nginx:
- [ ] Configure NGINX container to use environment variables_
      Note: If both Jenkins and Gerrit aren't both started, the NGINX
      container will continuously restart. This can be worked around by
      disabling the other service you don't want to use:
      `mv nginx/config/gerrit.conf gerrit.conf.disabled`

Nexus:
- [ ] Setup and configure Nexus

Gerrit:
- [ ] Remove postgres container configuration and replace with MariaDB
  (or make optional)

General:
- [ ] Setup OpenLDAP over SSL by default
- [ ] Create a basic ci-management repository in Gerrit
- [ ] Connect and configure Gerrit and Jenkins automatically
  - [ ]  Have the Gerrit configuration setup in Jenkins
  - [ ]  Create Gerrit user with ssh pubkey from Jenkins

Jenkins:
- [ ] Fix (on Jenkins restart)::
      WARNING: Caught exception evaluating:
      instance.hasExplicitPermission(attrs.sid,p) in /configureSecurity/.
      Reason: java.lang.NullPointerException
- [ ] Create a basic SSH Jenkins agent that jobs can be ran on
- [ ] Disable CLI remoting
- [ ] Enable Agent -> Master Access Controls

.. _environment: https://docs.docker.com/compose/environment-variables/#configuring-compose-using-environment-variables
.. _variables: https://docs.docker.com/samples/nginx/#using-environment-variables-in-nginx-configuration
.. _openfrontier: https://github.com/openfrontier/ci-compose
