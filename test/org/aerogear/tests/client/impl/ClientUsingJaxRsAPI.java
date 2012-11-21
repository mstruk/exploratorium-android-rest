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

import org.aerogear.tests.client.AbstractHttpClient;
import org.aerogear.tests.client.EndpointException;
import org.aerogear.tests.dao.Project;
import org.aerogear.tests.dao.Tag;
import org.aerogear.tests.dao.Task;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.aerogear.tests.util.Constants.ACCEPT;
import static org.aerogear.tests.util.Constants.APPLICATION_JSON;
import static org.aerogear.tests.util.Constants.AUTH_TOKEN;
import static org.aerogear.tests.util.Constants.CONTENT_TYPE;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class ClientUsingJaxRsAPI extends AbstractHttpClient {

    private Client client;
    private Response response;

    public ClientUsingJaxRsAPI() {
        System.setProperty("javax.ws.rs.ext.ClientFactory", "org.jboss.resteasy.client.jaxrs.internal.RestEasyClientFactory");
        client = ClientFactory.newClient();
    }

    @Override
    public void login() throws IOException {

        String url = toAbsoluteUrl(getLoginEndpoint());

        WebTarget target = client.target(url);

        // prepare HTTP request body - a simple JSON message
        String authInfo = composeAuthJSON();

        response = target.request()
            .header(ACCEPT, APPLICATION_JSON)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .buildPost(Entity.json(authInfo))
            .invoke();

        try {
            // check status for errors
            checkErrors(url);

            // get Auth-Token which we use to authenticate with CRUD endpoints
            String authToken = getSingleHeader(response.getHeaders().get(AUTH_TOKEN.toLowerCase()));

            // sanity check
            if (authToken == null) {
                throw new EndpointException("Failed to login with user '" + getUsername() + "' (200 OK, but no Auth-Token)!)");
            }
            setAuthToken(authToken);

        } finally {
            try {
                response.close();
            } catch (Exception ignored) {
            }
            response = null;
        }
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

    public <T> List<T> getEntities(String endpoint, Class<T> clazz) throws IOException {

        String url = toAbsoluteUrl(endpoint);
        WebTarget target = client.target(url);

        response = target.request()
            .header(ACCEPT, APPLICATION_JSON)
            .header(AUTH_TOKEN, getAuthToken())
            .buildGet()
            .invoke();

        try {
            // check status for errors
            checkErrors(url);

            // get Auth-Token which we use to authenticate with CRUD endpoints
            T[] res = response.readEntity(asArrayClass(clazz));
            return Arrays.asList(res);

        } finally {
            try {
                response.close();
            } catch (Exception ignored) {
            }
            response = null;
        }

    }


    @Override
    protected int getStatusCode() {
        checkResponseForNull();
        return response.getStatus();
    }

    @Override
    protected String getStatusMessage() {
        checkResponseForNull();
        return response.getStatusInfo().getReasonPhrase();
    }

    @Override
    protected String getResponseContentType() {
        checkResponseForNull();
        MediaType mediaType = response.getMediaType();
        if (mediaType == null) {
            return null;
        }
        if (mediaType.getSubtype() != null) {
            return mediaType.getType() + "/" + mediaType.getSubtype();
        }

        return mediaType.getType();
    }

    @Override
    protected String getResponseErrorBody() throws IOException {
        checkResponseForNull();
        return response.readEntity(String.class);
    }

    private void checkResponseForNull() {
        if (response == null) {
            throw new IllegalStateException("response is null");
        }
    }
}
