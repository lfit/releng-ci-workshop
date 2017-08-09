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

# Set the instance hostname
COPY hostname.groovy /usr/share/jenkins/ref/init.groovy.d/hostname.groovy

# Setup basic security
# TODO: Fix matrix permission causing NullPointerException ?
# COPY authorization.groovy /usr/share/jenkins/ref/init.groovy.d/authorization.groovy

COPY ldap.groovy /usr/share/jenkins/ref/init.groovy.d/ldap.groovy

# TODO: Prevent Cross Site Script Forgery
# TODO: Disable CLI over remoting
# TODO: Enable Agent/Master Access Control
# TODO: Disable JNLP Port
