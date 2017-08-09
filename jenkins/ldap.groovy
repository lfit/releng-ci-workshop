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
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import hudson.security.*

def instance = Jenkins.getInstance()

def LDAPRealm = new LDAPSecurityRealm("ldap://ldap",
"dc=example,dc=org",
"ou=Users",
"uid={0}",
"ou=Groups",
"cn=readonly,dc=example,dc=org",
"readonly",
false,
false
)
instance.setSecurityRealm(LDAPRealm)

def strategy = new GlobalMatrixAuthorizationStrategy()

strategy.add(Jenkins.ADMINISTER, 'sandbox-admins')

strategy.add(hudson.model.Hudson.READ,'anonymous')
strategy.add(hudson.model.Item.READ,'anonymous')
strategy.add(hudson.model.Item.WORKSPACE,'anonymous')
strategy.add(hudson.model.View.READ,'anonymous')

instance.setAuthorizationStrategy(strategy)

instance.save()
