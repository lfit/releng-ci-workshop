/*
 * SPDX-License-Identifier: EPL-1.0
 *
 * Copyright (c) 2017 The Linux Foundation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
// TODO: There is a race condition when having the creation of an agent
// and credentials in seperate scripts. The script to create an agent
// will run before the credentials exist and won't be able to use them,
// resulting in the need to restart jenkins container for them to be
// picked up. There may just be a 'save()' call or something similar
// missing, but this is the simplest approach to running the
// sequentially.
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import hudson.plugins.sshslaves.SSHLauncher;
import hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy;
import hudson.model.*
import hudson.slaves.*
import jenkins.model.*

def global_domain = Domain.global()
def store = Jenkins.getInstance().getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

// This key should NEVER be used elsewhere. It is hardcoded only because the
// publickey must be known ahead of time and passed as a variable to the docker
// container.
def	private_key = new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(
"""-----BEGIN RSA PRIVATE KEY-----
MIIEowIBAAKCAQEA1F9dbAwV2nAhPywXXFzuBNuzmW1rYzWnVzxiEUzdZXlBViZZ
5Y5WU99Vm5pfH76fuQSm1URz/1vSFo/z5QU8a8TjzDdkXIeNLWqz1NT8ViWDNtN5
+gHPhlinH0j4opJn14ifqSR7PtBNTFYaS3tPKW96lXb8ABnUF1Qud3YPXm62+Soq
2y71nRDHsH1ZcXQdq7tqd+fEDeWOxx7AKltbDK9fBtfzuW1SZe4NSW50Pv2I1WiU
aO3A7qJ0znmrZRDH2WV85Xq5oFwnFRUiKP6Crim7YpWdsnte2sfZDeK31QAFyHjg
FGVngmnd9hHUBDu8S/eemX7V8lfo5cNNwp3Q+QIDAQABAoIBAAZ+dAjdxb1MOHgK
DRzR6qVTYoaKhgIeneNZAVauFwcHUiwkOBOA6rrd1WxQqB/8YD30GnXjBfkFAcOW
20phgpt5Bc4002jQ7Ew7OwyDBsRLmVuP0+cFLydYhqO6Q4AVIf/BOcCeUPZ2wCZZ
a6xrNNx7gDAZ11LZd1bPSCx2+7lTcxk6QmGmHic4xWbCDJ+/jDdzirMj083VxeVg
JH26mbzCk/NQ4uhQqifxXkv8wCCpU1AdDl21sFDEJW2tgSoEruNN52TavXffH8f4
mnyzFV2MRI1R3wwXLZwEW/jHWQFruIU0LSkS9eNZORog+cuoi5N9IDiBdfebdRsl
gHvHQAECgYEA7wXML5phe/+t0xZqah4S/OS5igO/URhQ4gl+1rDH0EPQx6mM3l5Z
qe9cV6tq9/sFjbK4UX/1np45NQ9i+olpvTLwiDbBczW3ySjgkQpUpulO2AD5jJiX
aRlrbwN9WuL3T2P2WHRscvDVV0AMlVshOx09YSvDmoPjxtoUFP9iRgECgYEA43T7
aaLYveorsLOxasC07tRS2PUc1NrO9etaLjfGtUcKcdIzS12vs6CHtvocgjeFA6No
aF58ushuluQD7r9QD0Ao/IF8LAXnWXB/4Kp4inFbYuxJPdEMHC51dB1jmiomGepM
EGhCqzQvS4U2Tu5MKtKpai+wL4ri7JI/4DMruvkCgYB6nerFcNkZl2xAoXstvQfY
nC1iU9HNdD/p9R0QXdfjSybLhnsxiA1PU+93OgTB+hA7RLexd4c1O831HlOUWvHX
kU47Unui8qe5ljK9tSMADSfZP4bFTXI/BD9Mz+l6unxMSeeSMQeBX3LSM5VA+WLu
xG08cAsENSygUjeDHg/4AQKBgQDcoxlNux2r+38uBODQwOXB1kwXEI1LHIUtn4L2
2jvylFrZViFTtik9gTakk7Ebz2dDxDr/IsizFsHPtJbr/MBYStB3P9OHkKJ969bf
w/zxrkwLhVD2mdW5cIeWfvujC8ex08i9EaW6FQDbrPilUBqqX3be/itVss+005kK
jhiZYQKBgCC9Oe3im2PFfQjm1yRFeTRNJ+f31sk78aLEJk+KF6yv2dfDgC1ChU6a
QxR/mcboAaX1EvMLTjeFqDC5XK6gwC67s0H0p2gC91e6hzQs5Qb7M7g7LqD28YEW
Tja198bt428xTsjxxffuhekDQt8hbEO6RJFJPxhHQIWVo8708lWj
-----END RSA PRIVATE KEY-----""")

def private_key_on_master = new BasicSSHUserPrivateKey.UsersPrivateKeySource();

// Credentials for connecting to an SSH Jenkins Agent
def jenkins_agent_credentials = new BasicSSHUserPrivateKey(
  CredentialsScope.GLOBAL,
  "jenkins-ssh-key",
  "jenkins",
  private_key,
  null, // password
  null // description
)

// ID must match 'jenkins-ssh-credentials in Global-JJB
// Username must match Gerrit ssh user
def gerrit_credentials = new BasicSSHUserPrivateKey(
  CredentialsScope.GLOBAL,
  "ciworkshop-jenkins-ssh", // ID
  "jenkins-workshop", // username
  private_key_on_master,
  null, // password
  "Gerrit User" // description
)

store.addCredentials(global_domain, jenkins_agent_credentials)
store.addCredentials(global_domain, gerrit_credentials)

// Create Jenkins SSH Agent without verifying host key.
def ssh_strategy = new NonVerifyingKeyVerificationStrategy()
def ssh_launcher = new SSHLauncher(
    "jenkins-agent", 22, "jenkins-ssh-key",
    null, null,
    null, null,
    60, 10, 10,
    ssh_strategy
)

Slave slave = new DumbSlave("jenkins-agent1", "/home/jenkins", ssh_launcher)
slave.setNodeDescription("CI Workshop Jenkins SSH Agent")
slave.setRetentionStrategy(new RetentionStrategy.Always())
slave.setLabelString("ciworkshop")

Jenkins.getInstance().addNode(slave)
