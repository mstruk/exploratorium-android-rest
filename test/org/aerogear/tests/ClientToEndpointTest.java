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

import org.aerogear.tests.client.EndpointException;
import org.aerogear.tests.client.impl.ClientUsingHttpClientAPI;
import org.aerogear.tests.client.impl.ClientUsingJaxRsAPI;
import org.aerogear.tests.client.impl.ClientUsingURLConnectionAPI;
import org.aerogear.tests.dao.Project;
import org.aerogear.tests.dao.Tag;
import org.aerogear.tests.dao.Task;
import org.aerogear.tests.server.httpd.EmbeddedHttpServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class ClientToEndpointTest {

    private static final int port = 8381;

    @BeforeClass
    public static void setupHttpServer() throws IOException {
        EmbeddedHttpServer httpServer = new EmbeddedHttpServer();
        httpServer.start(port);
    }

    @Test
    public void httpClientTest() throws IOException {
        try {
            ClientUsingHttpClientAPI client = new ClientUsingHttpClientAPI();
            client.setRootURL("http://localhost:" + port + "/todo-server");

            // Log in as user 'test' via authentication endpoint: /auth/login
            client.login();

            // Get all Tasks
            List<Project> projects = client.getProjects();
            System.out.println("Got Projects: " + projects);

            // Get all Tasks
            List<Tag> tags = client.getTags();
            System.out.println("Got Tags: " + tags);

            // Get all Tasks
            List<Task> tasks = client.getTasks();
            System.out.println("Got Tasks: " + tasks);

        } catch (EndpointException ex) {
            System.out.println("Exception occurred: " + ex);
            System.out.println("Server message: " + ex.getServerMessage());
            throw ex;
        }
    }

    @Test
    public void urlConnectionTest() throws IOException {
        try {
            ClientUsingURLConnectionAPI client = new ClientUsingURLConnectionAPI();
            client.setRootURL("http://localhost:" + port + "/todo-server");

            // Log in as user 'test' via authentication endpoint: /auth/login
            client.login();

            // Get all Tasks
            List<Project> projects = client.getProjects();
            System.out.println("Got Projects: " + projects);

            // Get all Tasks
            List<Tag> tags = client.getTags();
            System.out.println("Got Tags: " + tags);

            // Get all Tasks
            List<Task> tasks = client.getTasks();
            System.out.println("Got Tasks: " + tasks);

        } catch (EndpointException ex) {
            System.out.println("Exception occurred: " + ex);
            System.out.println("Server message: " + ex.getServerMessage());
            throw ex;
        }
    }

    @Test
    public void jaxRsTest() throws IOException {
        try {
            ClientUsingJaxRsAPI client = new ClientUsingJaxRsAPI();
            client.setRootURL("http://localhost:" + port + "/todo-server");

            // Log in as user 'test' via authentication endpoint: /auth/login
            client.login();


            // Get all Tasks
            List<Project> projects = client.getProjects();
            System.out.println("Got Projects: " + projects);

            // Get all Tasks
            List<Tag> tags = client.getTags();
            System.out.println("Got Tags: " + tags);

            // Get all Tasks
            List<Task> tasks = client.getTasks();
            System.out.println("Got Tasks: " + tasks);


        } catch (EndpointException ex) {
            System.out.println("Exception occurred: " + ex);
            System.out.println("Server message: " + ex.getServerMessage());
            throw ex;
        }
    }
}
