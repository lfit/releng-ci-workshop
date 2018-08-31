# SPDX-License-Identifier: EPL-1.0
##############################################################################
# Copyright (c) 2017 The Linux Foundation and others.
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
##############################################################################
FROM centos:7

# Nodepool Dependencies
RUN yum install -y https://centos7.iuscommunity.org/ius-release.rpm \
 && yum clean all && rm -rf /var/cache/yum
RUN yum install -y @development git python35u python35u-pip python35u-devel java-1.8.0-openjdk libffi libffi-devel \
 && yum clean all && rm -rf /var/cache/yum
RUN yum install -y epel-release \
 && yum clean all && rm -rf /var/cache/yum

RUN alternatives --install /usr/bin/python3 python3 /usr/bin/python3.5 10
RUN alternatives --install /usr/bin/pip3 pip3 /usr/bin/pip3.5 10
RUN pip3 --no-cache-dir install python-openstackclient bindep

RUN pip3 --no-cache-dir install nodepool

# Nodepool Setup
RUN groupadd --system nodepool
RUN useradd --system nodepool --home-dir /var/lib/nodepool --create-home -g nodepool
RUN ssh-keygen -t rsa -N '' -b 2048 -f nodepool_rsa
RUN mkdir /etc/nodepool/
RUN mkdir /var/log/nodepool && chgrp -R nodepool /var/log/nodepool && chmod 755 /var/log/nodepool
RUN mkdir /var/run/nodepool && chown nodepool:nodepool /var/run/nodepool
