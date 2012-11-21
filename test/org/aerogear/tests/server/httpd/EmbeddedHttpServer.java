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

package org.aerogear.tests.server.httpd;

import org.aerogear.tests.dao.Project;
import org.aerogear.tests.dao.Tag;
import org.aerogear.tests.dao.Task;
import org.aerogear.tests.dao.User;
import org.aerogear.tests.server.ApplicationContext;
import org.aerogear.tests.server.CRUDServlet;
import org.aerogear.tests.server.LoginServlet;

import java.io.IOException;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class EmbeddedHttpServer {

    private static EmbeddedHttpServer server;

    private static final String LOGIN_ENDPOINT = "/todo-server/auth/login";
    private static final String PROJECTS_ENDPOINT = "/todo-server/projects";
    private static final String TAGS_ENDPOINT = "/todo-server/tags";
    private static final String TASKS_ENDPOINT = "/todo-server/tasks";

    public void start(int port) throws IOException {

        final Acme.Serve.Serve srv = new Acme.Serve.Serve();

        ApplicationContext applicationContext = new ApplicationContext();
        User testUser = new User("test", "tester");
        applicationContext.enroll(testUser);

        LoginServlet login = new LoginServlet(applicationContext);

        CRUDServlet projects = new CRUDServlet(applicationContext);
        CRUDServlet tags = new CRUDServlet(applicationContext);
        CRUDServlet tasks = new CRUDServlet(applicationContext);

        applicationContext.addCRUD(PROJECTS_ENDPOINT, Project.class);
        applicationContext.addCRUD(TAGS_ENDPOINT, Tag.class);
        applicationContext.addCRUD(TASKS_ENDPOINT, Task.class);

        Project project = new Project();
        project.setId("1");
        project.setTitle("Sample project");
        applicationContext.addEntity(project);

        Tag tag = new Tag();
        tag.setId("1");
        tag.setTitle("testing");
        applicationContext.addEntity(tag);

        Task task = new Task();
        task.setId("1");
        task.setTitle("Fix tests");
        task.setDate("2012-01-08");
        task.setDescription("Run tests, see which are failing, and fix them");
        applicationContext.addEntity(task);

        // setting properties for the server, and exchangeable Acceptors
        java.util.Properties properties = new java.util.Properties();
        properties.put("port", port);
        properties.setProperty(Acme.Serve.Serve.ARG_NOHUP, "nohup");
        srv.arguments = properties;

        srv.addServlet(LOGIN_ENDPOINT, login);
        srv.addServlet(PROJECTS_ENDPOINT, projects);
        srv.addServlet(TAGS_ENDPOINT, tags);
        srv.addServlet(TASKS_ENDPOINT, tasks);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    srv.notifyStop();

                } catch (Exception ignored) {
                }

                srv.destroyAllServlets();
            }
        }));

        srv.init();

        Thread acceptor = new Thread("TJWS Acceptor Thread") {
            @Override
            public void run() {
                srv.serve();
            }
        };
        acceptor.setDaemon(true);
        acceptor.start();
    }

    public synchronized static void provide() throws IOException {
        if (server == null) {
            server = new EmbeddedHttpServer();
            server.start(8380);
        }
    }
}
