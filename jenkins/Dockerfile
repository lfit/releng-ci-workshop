# SPDX-License-Identifier: EPL-1.0
##############################################################################
# Copyright (c) 2017 The Linux Foundation and others.
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
##############################################################################
FROM jenkins/jenkins:lts

# Disable upgrade wizard
RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state

# Install desired plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

ENV JENKINS_GROOVY_INIT /usr/share/jenkins/ref/init.groovy.d/

# Configure Basic Security
COPY basic-security-setup.groovy $JENKINS_GROOVY_INIT

# Configure LDAP
COPY ldap.groovy $JENKINS_GROOVY_INIT

# Configure Gerrit-Trigger
COPY gerrit-config.groovy $JENKINS_GROOVY_INIT

# Set Global Properties
COPY global-properties.groovy $JENKINS_GROOVY_INIT

# Create Jenkins SSH Agent
COPY create-jenkins-agent.groovy $JENKINS_GROOVY_INIT

# Create JJB INI in Config File Plugin
COPY create-jjbini.groovy $JENKINS_GROOVY_INIT

