/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aerogear.tests.client.impl;

import com.google.gson.Gson;
import org.aerogear.tests.client.AbstractHttpClient;
import org.aerogear.tests.client.EndpointException;
import org.aerogear.tests.dao.Project;
import org.aerogear.tests.dao.Tag;
import org.aerogear.tests.dao.Task;
import org.aerogear.tests.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.aerogear.tests.util.Constants.ACCEPT;
import static org.aerogear.tests.util.Constants.APPLICATION_JSON;
import static org.aerogear.tests.util.Constants.AUTH_TOKEN;
import static org.aerogear.tests.util.Constants.CONTENT_TYPE;
import static org.aerogear.tests.util.Constants.GET;
import static org.aerogear.tests.util.Constants.POST;
import static org.aerogear.tests.util.Constants.UTF_8;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class ClientUsingURLConnectionAPI extends AbstractHttpClient {

    private HttpURLConnection client;

    @Override
    public List<Task> getTasks() throws IOException {
        return getEntities(getTasksEndpoint(), Task.class);
    }

    @Override
    public List<Project> getProjects() throws IOException {
        return getEntities(getProjectsEndpoint(), Project.class);
    }

    @Override
    public List<Tag> getTags() throws IOException {
        return getEntities(getTagsEndpoint(), Tag.class);
    }

    public <T> List<T> getEntities(String endpoint, Class<T> clazz) throws IOException {

        String url = toAbsoluteUrl(endpoint);

        // prepare HTTP request headers
        URL endpointUrl = new URL(url);
        client = (HttpURLConnection) endpointUrl.openConnection();
        client.setRequestMethod(GET);
        client.setRequestProperty(ACCEPT, APPLICATION_JSON);
        client.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        applyAuthToken(client);

        // perform HTTP POST request
        InputStream response = client.getInputStream();
        try {
            // check status for errors
            checkErrors(url);

            Reader contentReader = new InputStreamReader(response, UTF_8);
            T[] ret = new Gson().fromJson(contentReader, asArrayClass(clazz));
            return Arrays.asList(ret);

        } finally {
            // defensive programming
            // make sure to fully consume response body ... should already be taken care of by Gson()
            // but it's a black box, and we don't know if maybe sometimes it leaves part of the body unconsumed
            try {
                IOUtils.copyAndCloseStream(response, null);
            } catch (Exception ignored) {
            }

            client = null;
        }
    }

    @Override
    public void login() throws IOException {
        // prepare HTTP request body - a simple JSON message
        String authInfo = composeAuthJSON();

        // prepare HTTP request headers
        String url = toAbsoluteUrl(getLoginEndpoint());
        URL endpoint = new URL(url);
        client = (HttpURLConnection) endpoint.openConnection();
        client.setRequestMethod(POST);
        client.setRequestProperty(ACCEPT, APPLICATION_JSON);
        client.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);

        // associate body with request
        initEntityBody(client, authInfo);

        // perform HTTP POST request
        InputStream response = client.getInputStream();
        try {
            // check status for errors
            checkErrors(url);

            // get Auth-Token which we use to authenticate with CRUD endpoints
            String authToken = getSingleHeader(client.getHeaderFields().get(AUTH_TOKEN.toLowerCase()));

            // sanity check
            if (authToken == null) {
                throw new EndpointException("Failed to login with user '" + getUsername() + "' (200 OK, but no Auth-Token)!)");
            }
            setAuthToken(authToken);

        } finally {
            // make sure to fully consume response body ...
            // login endpoint returns a JSON body. We throw it away.
            try {
                IOUtils.copyAndCloseStream(response, null);
            } catch (Exception ignored) {
            }

            client = null;
        }
    }

    @Override
    protected int getStatusCode() throws IOException {
        checkClientForNull();
        return client.getResponseCode();
    }

    @Override
    protected String getStatusMessage() throws IOException {
        checkClientForNull();
        return client.getResponseMessage();
    }

    @Override
    protected String getResponseContentType() {
        checkClientForNull();
        return client.getContentType();
    }

    @Override
    protected String getResponseErrorBody() throws IOException {
        checkClientForNull();
        Reader err = new InputStreamReader(client.getErrorStream(), UTF_8);
        StringWriter buff = new StringWriter();
        IOUtils.copyAndCloseStream(err, buff);
        return buff.toString();
    }

    private void checkClientForNull() {
        if (client == null) {
            throw new IllegalStateException("client is null");
        }
    }

    private void applyAuthToken(HttpURLConnection client) {
        String authToken = getAuthToken();
        if (authToken != null) {
            client.setRequestProperty(AUTH_TOKEN, authToken);
        }
    }

    private void initEntityBody(HttpURLConnection client, String content) throws IOException {
        client.setDoOutput(true);
        OutputStream output = client.getOutputStream();
        try {
            output.write(content.getBytes(UTF_8));
        } finally {
            output.close();
        }
    }
}
