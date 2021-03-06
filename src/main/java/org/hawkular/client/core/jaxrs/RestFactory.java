/*
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.client.core.jaxrs;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.hawkular.client.core.ClientInfo;
import org.hawkular.client.core.jaxrs.fasterxml.jackson.HCJacksonJson2Provider;
import org.hawkular.client.core.jaxrs.fasterxml.jackson.JacksonObjectMapperProvider;
import org.hawkular.client.core.jaxrs.param.ConvertersProvider;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class RestFactory<T> {
    private static final Logger _logger = LoggerFactory.getLogger(RestFactory.class);

    private final ClassLoader classLoader;
    private Class<T> apiClassType;

    public RestFactory(Class<T> clz) {
        classLoader = null;
        apiClassType = clz;
    }

    public RestFactory(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public T createAPI(ClientInfo clientInfo) {
        final HttpClient httpclient;
        if (clientInfo.getEndpointUri().toString().startsWith("https")) {
            httpclient = getHttpClient();
        } else {
            httpclient = HttpClientBuilder.create().build();
        }
        final ResteasyClient client;
        if (clientInfo.getUsername().isPresent() && clientInfo.getPassword().isPresent()) {
            HttpHost targetHost = new HttpHost(clientInfo.getEndpointUri().getHost(), clientInfo.getEndpointUri().getPort());
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials(clientInfo.getUsername().get(), clientInfo.getPassword().get()));
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);
            // Add AuthCache to the execution context
            HttpClientContext context = HttpClientContext.create();
            context.setCredentialsProvider(credsProvider);
            context.setAuthCache(authCache);
            ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(httpclient, context);

            client = new ResteasyClientBuilder().httpEngine(engine).build();
        } else {
            ApacheHttpClient4Engine engine = new ApacheHttpClient4Engine(getHttpClient());
            client = new ResteasyClientBuilder().httpEngine(engine).build();
        }

        client.register(JacksonJaxbJsonProvider.class);
        client.register(JacksonObjectMapperProvider.class);
        client.register(RequestLoggingFilter.class);
        client.register(new RequestHeadersFilter(clientInfo.getHeaders()));
        client.register(ResponseLoggingFilter.class);
        client.register(HCJacksonJson2Provider.class);
        client.register(ConvertersProvider.class);

        ProxyBuilder<T> proxyBuilder = client.target(clientInfo.getEndpointUri()).proxyBuilder(apiClassType);
        if (classLoader != null) {
            proxyBuilder = proxyBuilder.classloader(classLoader);
        }
        return proxyBuilder.build();
    }

    //trust any host
    private HttpClient getHttpClient() {
        CloseableHttpClient httpclient = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(keyStore, (TrustStrategy) (trustedCert, nameConstraints) -> true);

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            _logger.error("Failed to create HTTPClient: {}", e);
        }

        return httpclient;
    }
}
