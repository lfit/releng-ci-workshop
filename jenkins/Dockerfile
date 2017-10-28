# SPDX-License-Identifier: EPL-1.0
##############################################################################
# Copyright (c) 2017 The Linux Foundation and others.
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
##############################################################################
ARG JENKINS_VERSION=lts

FROM jenkins/jenkins:${JENKINS_VERSION}

# Disable upgrade wizard
RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state

# Install desired plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# Download PostBuildScript Plugin
RUN curl --fail -sSL \
  http://ftp.osuosl.org/pub/jenkins/plugins/postbuildscript/0.17/postbuildscript.hpi \
  -o /usr/share/jenkins/ref/plugins/postbuildscript.hpi

ENV JENKINS_GROOVY_INIT /usr/share/jenkins/ref/init.groovy.d/

# Configure Basic Security
COPY scripts/basic-security-setup.groovy $JENKINS_GROOVY_INIT

# Configure LDAP
COPY scripts/ldap.groovy $JENKINS_GROOVY_INIT

# Configure Gerrit-Trigger
COPY scripts/gerrit-config.groovy $JENKINS_GROOVY_INIT

# Set Global Properties
COPY scripts/global-properties.groovy $JENKINS_GROOVY_INIT

# Create Jenkins SSH Agent
COPY scripts/create-jenkins-agent.groovy $JENKINS_GROOVY_INIT

# Create JJB INI in Config File Plugin
COPY scripts/create-jjbini.groovy $JENKINS_GROOVY_INIT

# Create Nexus settings.xml
COPY scripts/create-nexus-settings.groovy $JENKINS_GROOVY_INIT
