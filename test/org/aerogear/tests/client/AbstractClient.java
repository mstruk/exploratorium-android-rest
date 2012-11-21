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

package org.aerogear.tests.client;

import org.aerogear.tests.dao.Project;
import org.aerogear.tests.dao.Tag;
import org.aerogear.tests.dao.Task;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public abstract class AbstractClient {

    //String rootUrl = "http://todo-aerogear.rhcloud.com/todo-server";
    private String rootUrl = "http://localhost:8080/todo-server";
    private String loginEndpoint = "/auth/login";
    private String projectsEndpoint = "/projects";
    private String tagsEndpoint = "/tags";
    private String tasksEndpoint = "/tasks";

    private String user = "test";
    private String pass = "tester";

    private String authToken;


    public void setRootURL(String url) {
        this.rootUrl = url;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public boolean isLoggedIn() {
        return authToken != null;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setUsername(String username) {
        this.user = username;
    }

    public String getUsername() {
        return user;
    }

    public void setPassword(String password) {
        this.pass = password;
    }

    public String getPassword() {
        return pass;
    }

    public void setLoginEndpoint(String endpoint) {
        this.loginEndpoint = endpoint;
    }

    public String getLoginEndpoint() {
        return loginEndpoint;
    }

    public void setTasksEndpoint(String endpoint) {
        this.tasksEndpoint = endpoint;
    }

    public String getTasksEndpoint() {
        return tasksEndpoint;
    }

    public void setTagsEndpoint(String endpoint) {
        this.tagsEndpoint = endpoint;
    }

    public String getTagsEndpoint() {
        return tagsEndpoint;
    }

    public void setProjectsEndpoint(String endpoint) {
        this.projectsEndpoint = endpoint;
    }

    public String getProjectsEndpoint() {
        return projectsEndpoint;
    }

    public String toAbsoluteUrl(String endpoint) {
        return rootUrl + endpoint;
    }

    protected final <T> Class<T[]> asArrayClass(Class<T> klass) {
        return (Class<T[]>) (Array.newInstance(klass, 1)).getClass();
    }

    protected String getSingleHeader(List<? extends Object> values) {
        if (values == null || values.size() != 1) {
            throw new RuntimeException("Expected one header, found: " + (values == null ? 0: values.size()));
        }
        return String.valueOf(values.get(0));
    }

    public abstract List<Task> getTasks() throws IOException;

    public abstract List<Project> getProjects() throws IOException;

    public abstract List<Tag> getTags() throws IOException;

    public abstract void login() throws IOException;
}
