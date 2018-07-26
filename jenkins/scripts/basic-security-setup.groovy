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
import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration
import jenkins.security.s2m.AdminWhitelistRule
import hudson.security.csrf.DefaultCrumbIssuer

def instance = Jenkins.getInstance();

def env = System.getenv()
String JENKINS_URL = env.get('JENKINSWEBURL')

// Enable Crumb issuer for CSRF protection
instance.setCrumbIssuer(new DefaultCrumbIssuer(true))

// Disable CLI Over Remoting
instance.getDescriptor("jenkins.CLI").get().setEnabled(false)

// Enable Agent -> Master subsystem
instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

// Set the default URL

// Below code does nothing, jenkins is using some other way to guess the root url
// Oddly it adds a trailing slash when it does
// that leads to a warning on the console
//def jlc = JenkinsLocationConfiguration.get()
//jlc.setUrl("JENKINS_URL", JENKINS_URL)
//jlc.save()

instance.save()
