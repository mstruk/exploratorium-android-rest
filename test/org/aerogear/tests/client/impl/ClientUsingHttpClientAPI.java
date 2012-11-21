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
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static org.aerogear.tests.util.Constants.ACCEPT;
import static org.aerogear.tests.util.Constants.APPLICATION_JSON;
import static org.aerogear.tests.util.Constants.AUTH_TOKEN;
import static org.aerogear.tests.util.Constants.CONTENT_TYPE;
import static org.aerogear.tests.util.Constants.UTF_8;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class ClientUsingHttpClientAPI extends AbstractHttpClient {

    private HttpClient client;
    private HttpResponse response;

    public ClientUsingHttpClientAPI() {
        client = new DefaultHttpClient();
    }

    public void execute() throws IOException {

        // Log in as user 'test' via authentication endpoint: /auth/login
        login();

        // Get all Tasks
        List<Project> projects = getProjects();
        System.out.println("Got Projects: " + projects);

        // Get all Tasks
        List<Tag> tags = getTags();
        System.out.println("Got Tags: " + tags);

        // Get all Tasks
        List<Task> tasks = getTasks();
        System.out.println("Got Tasks: " + tasks);
    }

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

    @Override
    public void login() throws IOException {

        // prepare HTTP request body - a simple JSON message
        String authInfo = composeAuthJSON();

        // prepare HTTP request headers
        String url = toAbsoluteUrl(getLoginEndpoint());
        HttpPost postMethod = new HttpPost(url);
        postMethod.setHeader(ACCEPT, APPLICATION_JSON);
        postMethod.setHeader(CONTENT_TYPE, APPLICATION_JSON);

        // associate body with request
        initEntityBody(postMethod, authInfo);

        // perform HTTP POST request
        response = client.execute(postMethod);
        try {
            // check status for errors
            checkErrors(url);

            // get Auth-Token which we use to authenticate with CRUD endpoints
            Header authTokenHeader = getSingleHeader(response.getHeaders(AUTH_TOKEN));
            String authToken = authTokenHeader.getValue();

            // sanity check
            if (authToken == null) {
                throw new EndpointException("Failed to login with user '" + getUsername() + "' (200 OK, but no Auth-Token)!)");
            }
            setAuthToken(authToken);

        } finally {
            // make sure to fully consume response body ...
            // login endpoint returns a JSON body. We throw it away.
            try {
                response.getEntity().consumeContent();

            } catch (Exception ignored) {
            }
            response = null;
        }
    }

    public <T> List<T> getEntities(String endpoint, Class<T> clazz) throws IOException {

        String url = toAbsoluteUrl(endpoint);

        HttpGet method = new HttpGet(url);
        method.setHeader(ACCEPT, APPLICATION_JSON);
        method.setHeader(CONTENT_TYPE, APPLICATION_JSON);
        applyAuthToken(method);

        response = client.execute(method);
        checkErrors(url);

        HttpEntity entity = response.getEntity();
        Reader contentReader = new InputStreamReader(entity.getContent(), UTF_8);
        try {

            T[] ret = new Gson().fromJson(contentReader, asArrayClass(clazz));
            return Arrays.asList(ret);

        } finally {
            // defensive programming
            // make sure to fully consume response body ... should already be taken care of by Gson()
            // but it's a black box, and we don't know if maybe sometimes it leaves part of the body unconsumed
            try {
                response.getEntity().consumeContent();
            } catch (Exception ignored) {
            }

            response = null;
        }
    }


    @Override
    protected int getStatusCode() {
        checkResponseForNull();
        return response.getStatusLine().getStatusCode();
    }

    private void checkResponseForNull() {
        if (response == null) {
            throw new IllegalStateException("response is null");
        }
    }

    @Override
    protected String getStatusMessage() {
        checkResponseForNull();
        return response.getStatusLine().getReasonPhrase();
    }

    @Override
    protected String getResponseContentType() {
        checkResponseForNull();
        Header header = response.getEntity().getContentType();
        if (header != null) {
            return header.getValue();
        }

        return null;
    }

    @Override
    protected String getResponseErrorBody() throws IOException {
        checkResponseForNull();
        HttpEntity entity = response.getEntity();
        byte[] buf = EntityUtils.toByteArray(entity);
        return new String(buf, UTF_8);
    }


    private void applyAuthToken(HttpGet method) {
        String authToken = getAuthToken();
        if (authToken != null) {
            method.setHeader(AUTH_TOKEN, authToken);
        }
    }

    private Header getSingleHeader(Header[] headers) {
        if (headers.length != 1) {
            throw new RuntimeException("Expected one header, found: " + headers.length);
        }
        return headers[0];
    }

    private void initEntityBody(HttpPost postMethod, String authInfo) throws UnsupportedEncodingException {
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(authInfo.getBytes(UTF_8)));
        postMethod.setEntity(entity);
    }
}
