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
import jenkins.model.*
import hudson.markup.RawHtmlMarkupFormatter

instance = Jenkins.getInstance()
globalNodeProperties = instance.getGlobalNodeProperties()
envVarsNodePropertyList =
    globalNodeProperties.getAll(
        hudson.slaves.EnvironmentVariablesNodeProperty.class)

def env = System.getenv()

String GIT_URL = env.get('JJB_GIT_URL')
String NEXUS_URL = env.get('JJB_NEXUS_URL')
String NEXUSPROXY = env.get('JJB_NEXUSPROXY')
String LOGS_SERVER = env.get('JJB_LOGS_SERVER')
String SILO = env.get('JJB_SILO')
String JENKINS_HOSTNAME = env.get('JJB_JENKINS_HOSTNAME')

// Set Markup Formatter to 'Safe HTML' so nexus build log links show up.
instance.setMarkupFormatter(new RawHtmlMarkupFormatter(false))

newEnvVarsNodeProperty = null
envVars = null

if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
  newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty();
  globalNodeProperties.add(newEnvVarsNodeProperty)
  envVars = newEnvVarsNodeProperty.getEnvVars()
} else {
  envVars = envVarsNodePropertyList.get(0).getEnvVars()

}

(GIT_URL != null) && envVars.put("GIT_URL", GIT_URL)
(NEXUS_URL != null) && envVars.put("NEXUS_URL", NEXUS_URL)
(NEXUSPROXY != null) && envVars.put("NEXUSPROXY", NEXUSPROXY)
(LOGS_SERVER != null) && envVars.put("LOGS_SERVER", LOGS_SERVER)
(SILO != null) && envVars.put("SILO", SILO)
(JENKINS_HOSTNAME != null) && envVars.put("JENKINS_HOSTNAME", JENKINS_HOSTNAME)

instance.save()
