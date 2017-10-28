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
import com.cloudbees.plugins.credentials.SystemCredentialsProvider;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import org.jenkinsci.plugins.configfiles.GlobalConfigFiles;
import org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig;
import org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig.MavenSettingsConfigProvider;
import org.jenkinsci.plugins.configfiles.maven.security.ServerCredentialMapping;
import jenkins.model.Jenkins;

def store = SystemCredentialsProvider.getInstance().getStore();

Credentials nexus_credentials =
    (Credentials) new UsernamePasswordCredentialsImpl(
        CredentialsScope.GLOBAL,
        "nexus-credentials",
        "Nexus Repository Credentials",
        "admin",
        "admin123");

// Create Nexus credentials
store.addCredentials(Domain.global(), nexus_credentials);


// Need to wait until after plugins are loaded
Thread.start {
    sleep 10000
    println "--> Creating the Nexus Settings file"

    GlobalConfigFiles config_files_store = Jenkins.getInstance()
        .getExtensionList(GlobalConfigFiles.class).get(GlobalConfigFiles.class);

    ServerCredentialMapping server_credentials = new ServerCredentialMapping(
        "logs",
        "nexus-credentials");
    List<ServerCredentialMapping> server_mapping =
        new ArrayList<ServerCredentialMapping>();
    server_mapping.add(server_credentials);

    MavenSettingsConfigProvider settings_config =
        new MavenSettingsConfigProvider();

    MavenSettingsConfig config = new MavenSettingsConfig(
        "jenkins-log-archives-settings",
        "jenkins-logs",
        "Nexus Repository",
        settings_config.loadTemplateContent(),
        MavenSettingsConfig.isReplaceAllDefault,
        server_mapping);
    config_files_store.save(config);
}
