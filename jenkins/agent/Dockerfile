FROM jenkinsci/ssh-slave

RUN DEBIAN_FRONTEND=noninteractive \
    apt-get update \
    && apt-get install --no-install-recommends -y python python-pip \
       python-tox python-dev \
       build-essential zip facter xmlstarlet \
       apt-transport-https ca-certificates \
       curl software-properties-common

RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
RUN add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/debian \
   $(lsb_release -cs) \
   stable"

RUN DEBIAN_FRONTEND=noninteractive \
    apt-get update \
    && apt-get install -y docker-ce \
    && apt-get clean

RUN /usr/bin/pip install --upgrade setuptools
RUN /usr/bin/pip install virtualenv

# Set the docker group to my host docker group ID
RUN groupmod -g 129 docker
RUN usermod -aG docker jenkins
