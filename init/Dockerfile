# SPDX-License-Identifier: EPL-1.0
##############################################################################
# Copyright (c) 2017 The Linux Foundation and others.
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
##############################################################################
FROM python:3

ENV CI_SYSTEM=jenkins
ENV ANSIBLE_STDOUT_CALLBACK=yaml

RUN DEBIAN_FRONTEND=noninteractive apt-get update && apt-get install -yq \
  openjdk-11-jdk \
  python3-apt \
  && apt-get clean
RUN pip install --no-cache-dir ansible docker-py

RUN mkdir /etc/ansible
RUN mkdir /usr/src/ansible

WORKDIR /usr/src/ansible

CMD ["ansible-playbook", "-v", "/usr/src/ansible/site.yaml", "-i", "/etc/ansible/inventory.ini"]
