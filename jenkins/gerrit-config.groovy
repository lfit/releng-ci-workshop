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
import com.sonyericsson.hudson.plugins.gerrit.trigger.GerritServer;
import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritConnectionListener;
import com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl;
import com.sonyericsson.hudson.plugins.gerrit.trigger.config.Config;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

Thread.start {
    sleep 10000
    println "--> Configuring the CI Workshop Gerrit Server"
    def GerritServerName = "ciworkshop"
    def plugin = PluginImpl.getInstance()

    GerritServer gerritServer = new GerritServer(GerritServerName);

    def configJSONString = """{"gerritFrontEndUrl":"http://gerrit",
    "gerritHostName":"gerrit",
    "gerritSshPort":"29418",
    "gerritUserName":"jenkins-workshop",
    "verdictCategories":[
    {"verdictValue":"Code-Review","verdictDescription":"Code Review"},
    {"verdictValue":"Verified","verdictDescription":"Verified"}],
    "gerritBuildStartedVerifiedValue":"0",
    "gerritBuildSuccessfulVerifiedValue":"1",
    "gerritBuildFailedVerifiedValue":"-1",
    "gerritBuildUnstableVerifiedValue":"0",
    "gerritBuildNotBuiltVerifiedValue":"0",
    "gerritBuildStartedCodeReviewValue":"0",
    "gerritBuildSuccessfulCodeReviewValue":"0",
    "gerritBuildFailedCodeReviewValue":"0",
    "gerritBuildUnstableCodeReviewValue":"-1",
    "gerritBuildNotBuiltCodeReviewValue":"0"}"""
    JSONObject configObject = (JSONObject)JSONSerializer.toJSON(configJSONString);
    Config config = new Config(configObject);

    gerritServer.setConfig(config);
    gerritServer.addListener(new GerritConnectionListener(GerritServerName));

    if (plugin.containsServer(GerritServerName)) {
        plugin.removeServer(plugin.getServer(GerritServerName))
    }
    plugin.addServer(gerritServer)

    gerritServer.start()
    gerritServer.startConnection()
    println "--> Configuring the CI Workshop Gerrit Server...done"
}
