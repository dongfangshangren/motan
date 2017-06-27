/*
 * Copyright 2009-2016 Weibo, Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.weibo.api.motan.protocol.restful.support.undertow;

import com.weibo.api.motan.protocol.restful.EmbedRestServer;
import com.weibo.api.motan.rpc.URL;
import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * 使用 Undertow 的 RestServer
 *
 * @author wangjunwei
 * @since 2017-06-26
 */
public class UndertowServer extends EmbedRestServer {
    private final URL url;
    private final ResteasyDeployment resteasyDeployment = new ResteasyDeployment();
    private final UndertowJaxrsServer undertowJaxrsServer = new UndertowJaxrsServer();

    public UndertowServer(URL url) {
        super(null);
        this.url = url;
    }

    @Override
    public void start() {
        resteasyDeployment.start();

        final DeploymentInfo deploymentInfo = undertowJaxrsServer.undertowDeployment(resteasyDeployment)
                .setContextPath("/")
                .setDeploymentName("motan-restful")
                .setClassLoader(Thread.currentThread().getContextClassLoader());

        undertowJaxrsServer.deploy(deploymentInfo).start(Undertow.builder().addHttpListener(url.getPort(), url.getHost()));
    }

    @Override
    public ResteasyDeployment getDeployment() {
        return resteasyDeployment;
    }

    @Override
    public void stop() {
        resteasyDeployment.stop();
        undertowJaxrsServer.stop();
    }
}
