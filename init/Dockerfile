# SPDX-License-Identifier: EPL-1.0
##############################################################################
# Copyright (c) 2017 The Linux Foundation and others.
#
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
##############################################################################

FROM ubuntu:14.04

RUN DEBIAN_FRONTEND=noninteractive \
    apt-get update -qq \
    && apt-get install --no-install-recommends -y \
       curl git python python-pip python-dev libyaml-dev \
       realpath openssh-client \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir /docker-entrypoint-init.d/

WORKDIR /docker-entrypoint-init.d/

RUN /usr/bin/curl -sSL -O https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh
RUN /bin/chmod +x wait-for-it.sh

COPY config-ci-environment.sh /docker-entrypoint-init.d/

RUN chmod +x /docker-entrypoint-init.d/*.sh

CMD ["/docker-entrypoint-init.d/config-ci-environment.sh"]
