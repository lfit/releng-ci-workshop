FROM jenkinsci/ssh-slave

RUN DEBIAN_FRONTEND=noninteractive \
    apt-get update && \
    apt-get install --no-install-recommends -y \
       build-essential \
       facter \
       libffi-dev \
       libssl-dev \
       python \
       python-wheel \
       python-pip \
       python-tox \
       python-dev \
       xmlstarlet \
       zip && \
    apt-get clean

RUN /usr/bin/pip install --upgrade setuptools
RUN /usr/bin/pip install --upgrade virtualenv
RUN /usr/bin/pip install --upgrade cryptography
RUN /usr/bin/pip install --upgrade ansible
