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

package org.aerogear.tests;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.aerogear.android.core.HttpException;
import org.aerogear.tests.client.impl.ClientUsingAerogearAPI;
import org.aerogear.tests.dao.Project;
import org.aerogear.tests.dao.Tag;
import org.aerogear.tests.dao.Task;
import org.aerogear.tests.server.httpd.EmbeddedHttpServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
@RunWith(RobolectricTestRunner.class)
public class AndroidClientToEndpointTest {

    @Before
    public void setupHttpServer() throws IOException {
        System.out.println("Java version: " + System.getProperty("java.version"));
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
        EmbeddedHttpServer.provide();
    }

    @Test
    public void aerogearClientTest() throws IOException {
        try {
            ClientUsingAerogearAPI client = new ClientUsingAerogearAPI();
            client.setRootURL("http://localhost:8380/todo-server");
            client.init();

            //prepareLoginResponse(client);
            // Log in as user 'test' via authentication endpoint: /auth/login
            client.login();
            System.out.println("Logged in ...");

            //prepareGetProjectsResponse();
            List<Project> projects = client.getProjects();
            System.out.println("Projects: " + projects);

            //prepareGetTagsResponse();
            List<Tag> tags = client.getTags();
            System.out.println("Tags: " + tags);

            //prepareGetTasksResponse();
            List<Task> tasks = client.getTasks();
            System.out.println("Tasks: " + tasks);

        } catch (HttpException ex) {
            System.out.println("Exception occurred: " + ex);
            System.out.println("Server message: " + ex.getStatusCode() + " " + ex.getMessage());
            System.out.println(ex.getData() == null ? "": new String(ex.getData(), "utf-8"));
            throw ex;
        }
    }

    /*
    private void prepareGetTasksResponse() {

    }

    private void prepareGetTagsResponse() {

    }

    private void prepareGetProjectsResponse() {
        String body = "[{\"id\":1,\"title\":\"Sample Project\",\"style\":\"project-232-21-21\",\"tasks\":[1]}]";
        TestHttpResponse response = new TestHttpResponse(200, body);
        response.addHeader(new BasicHeader("Content-Type", "application/json"));

        Robolectric.addPendingHttpResponse(response);
    }

    private void prepareLoginResponse(ClientUsingAerogearAPI client) {
        String body = "{\"username\":\"test\",\"roles\":[\"admin\"],\"logged\":\"true\"}";
        TestHttpResponse response = new TestHttpResponse(200, body);
        response.addHeader(new BasicHeader("Content-Type", "application/json"));
        response.addHeader(new BasicHeader("Auth-Token", client.getAuthToken()));

        Robolectric.addPendingHttpResponse(response);
    }
    */
}
