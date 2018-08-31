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

# Zuul & Nodepool Dependencies
RUN yum install -y https://centos7.iuscommunity.org/ius-release.rpm \
 && yum clean all && rm -rf /var/cache/yum
RUN yum install -y git python35u python35u-pip python35u-devel java-1.8.0-openjdk \
 && yum clean all && rm -rf /var/cache/yum
RUN yum group install -y "Development Tools" \
 && yum clean all && rm -rf /var/cache/yum
RUN yum install -y epel-release \
 && yum clean all && rm -rf /var/cache/yum
RUN yum install -y re2-devel \
 && yum clean all && rm -rf /var/cache/yum

RUN alternatives --install /usr/bin/python3 python3 /usr/bin/python3.5 10
RUN alternatives --install /usr/bin/pip3 pip3 /usr/bin/pip3.5 10
RUN pip3 --no-cache-dir install python-openstackclient bindep
RUN pip3 --no-cache-dir install zuul

# Nodepool Setup
RUN groupadd --system nodepool
RUN useradd --system nodepool --home-dir /var/lib/nodepool --create-home -g nodepool
RUN mkdir /etc/nodepool/
RUN mkdir /var/log/nodepool && chgrp -R nodepool /var/log/nodepool && chmod 755 /var/log/nodepool

# Zuul Setup
RUN groupadd --system zuul
RUN useradd --system zuul --home-dir /var/lib/zuul --create-home -g zuul
RUN mkdir /etc/zuul/
RUN mkdir /var/log/zuul/ && chown zuul:zuul /var/log/zuul
RUN mkdir /var/lib/zuul/.ssh && chmod 0700 /var/lib/zuul/.ssh
RUN chown -R zuul:zuul /var/lib/zuul/.ssh

RUN mkdir /var/run/zuul && chown zuul:zuul /var/run/zuul
