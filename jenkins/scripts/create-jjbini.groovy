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
import org.jenkinsci.plugins.configfiles.GlobalConfigFiles;
import org.jenkinsci.plugins.configfiles.custom.CustomConfig;
import jenkins.model.Jenkins;

// Need to wait until after plugins are loaded
Thread.start {
    sleep 10000
    println "--> Creating the JJB INI"

    GlobalConfigFiles store = Jenkins.getInstance().getExtensionList(GlobalConfigFiles.class).get(GlobalConfigFiles.class);
    CustomConfig config = new CustomConfig("jjbini", "jenkins-jjb-ini", "Jenkins Job Builder Config","""[job_builder]
ignore_cache=True
keep_descriptions=False
include_path=.:scripts:~/git/
recursive=True

[jenkins]
url=http://jenkins:8080/
user=sandbox
password=sandbox
query_plugins_info=True""");
    store.save(config);

    println "--> Creating the NPM RC File"
    CustomConfig npm_config = new CustomConfig("npmrc", "npmrc-file", "NPM RC File","")
    store.save(npm_config);

    println "--> Creating the Pip Conf File"
    CustomConfig pip_config = new CustomConfig("pipconf", "pipconf-file", "Pip Conf","")
    store.save(pip_config);
}
