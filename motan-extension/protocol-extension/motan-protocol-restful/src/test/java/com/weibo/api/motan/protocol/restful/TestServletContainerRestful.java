/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.protocol.restful;

import com.weibo.api.motan.protocol.restful.support.servlet.RestfulServletContainerListener;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;

import java.io.File;
import java.util.Collections;
import java.util.Map;

/**
 * servlet容器下restful协议测试
 *
 * @author zhouhaocheng
 *
 */
public class TestServletContainerRestful extends TestRestful {
	private Tomcat tomcat;

	private final String contextpath = "/cp";
	private final String servletPrefix = "/servlet";

	@Override
	protected void beforeInit() throws LifecycleException {
		tomcat = new Tomcat();
		String baseDir = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
		tomcat.setBaseDir(baseDir);
		tomcat.setPort(getPort());

		tomcat.getConnector().setProperty("URIEncoding", "UTF-8");
		tomcat.getConnector().setProperty("socket.soReuseAddress", "true");
		tomcat.getConnector().setProperty("connectionTimeout", "20000");

		/**
		 * <pre>
		 *
		 * <servlet>
		 *  <servlet-name>dispatcher</servlet-name>
		 *  <servlet-class>org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher</servlet-class>
		 *  <load-on-startup>1</load-on-startup>
		 *  <init-param>
		 *    <param-name>resteasy.servlet.mapping.prefix</param-name>
		 *    <param-value>/servlet</param-value>  <!-- 此处实际为servlet-mapping的url-pattern，具体配置见resteasy文档-->
		 *  </init-param>
		 * </servlet>
		 *
		 * <servlet-mapping>
		 *   <servlet-name>dispatcher</servlet-name>
		 *   <url-pattern>/servlet/*</url-pattern>
		 * </servlet-mapping>
		 *
		 * </pre>
		 */
		Context context = tomcat.addContext(contextpath, baseDir);
		Wrapper wrapper = Tomcat.addServlet(context, "dispatcher", HttpServletDispatcher.class.getName());
		wrapper.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, servletPrefix);
		wrapper.setLoadOnStartup(1);
		context.addServletMapping(servletPrefix + "/*", "dispatcher");

		/**
		 * <listener>
		 * <listener-class>com.weibo.api.motan.protocol.restful.support.servlet.RestfulServletContainerListener</listener-class>
		 * </listener>
		 */
		context.addApplicationListener(RestfulServletContainerListener.class.getName());

		tomcat.start();
	}

	@Override
	protected String getEndpointFactory() {
		return "servlet";
	}

	@Override
	protected Map<String, String> getProtocolExtParameters() {
		return Collections.singletonMap("contextpath", contextpath + servletPrefix);
	}

	@Override
	protected void clean() throws Exception {
		tomcat.stop();
		tomcat.destroy();
	}
}
