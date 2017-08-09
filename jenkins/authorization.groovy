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
import hudson.*
import hudson.model.*
import jenkins.model.*
import hudson.security.*

// Gets Admin username and password from environment
//  and sets authentication to Jenkins' own user database,
//  and the authorization to Matrix-based security.
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
def admin_user = System.getenv('JENKINS_ADMIN_USER')
def admin_password = System.getenv('JENKINS_ADMIN_PASSWORD')
hudsonRealm.createAccount(admin_user, admin_password)

def instance = Jenkins.getInstance()
instance.setSecurityRealm(hudsonRealm)
instance.save()

def strategy = new GlobalMatrixAuthorizationStrategy()

// Set Anonymous Permissions
strategy.add(Jenkins.READ,'anonymous')
strategy.add(Item.READ,'anonymous')
strategy.add(Item.WORKSPACE,'anonymous')
strategy.add(View.READ,'anonymous')

// Set Admin Permissions
strategy.add(Jenkins.ADMINISTER, "admin")

instance.setAuthorizationStrategy(strategy)
instance.save()
