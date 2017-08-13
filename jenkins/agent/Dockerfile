FROM jenkinsci/ssh-slave

RUN DEBIAN_FRONTEND=noninteractive \
    apt-get update \
    && apt-get install --no-install-recommends -y python python-pip \
    && apt-get clean

RUN /usr/bin/pip install virtualenv
