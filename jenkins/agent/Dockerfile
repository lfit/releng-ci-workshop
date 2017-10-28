FROM jenkinsci/ssh-slave

RUN DEBIAN_FRONTEND=noninteractive \
    apt-get update \
    && apt-get install --no-install-recommends -y python python-pip \
       python-tox python-dev \
       build-essential zip facter xmlstarlet \
    && apt-get clean

RUN /usr/bin/pip install --upgrade setuptools
RUN /usr/bin/pip install virtualenv
