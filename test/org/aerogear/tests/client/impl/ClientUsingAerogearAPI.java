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

import org.aerogear.android.Callback;
import org.aerogear.android.Pipeline;
import org.aerogear.android.authentication.AuthenticationModule;
import org.aerogear.android.authentication.impl.Authenticator;
import org.aerogear.android.authentication.impl.RestAuthenticationConfig;
import org.aerogear.android.core.HeaderAndBody;
import org.aerogear.android.impl.pipeline.PipeConfig;
import org.aerogear.android.pipeline.Pipe;
import org.aerogear.tests.client.AbstractClient;
import org.aerogear.tests.dao.Project;
import org.aerogear.tests.dao.Tag;
import org.aerogear.tests.dao.Task;
import org.aerogear.tests.util.FutureResult;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class ClientUsingAerogearAPI extends AbstractClient {

    private Pipeline pipeline;
    private AuthenticationModule authModule;
    private String authToken;

    private Pipe<Project> projectPipe;
    private Pipe<Tag> tagPipe;
    private Pipe<Task> taskPipe;

    public void init() throws MalformedURLException {

        URL url = new URL(getRootUrl());
        pipeline = new Pipeline(url);

        PipeConfig config = new PipeConfig(url, Project.class);
        config.setName(getProjectsEndpoint());
        config.setEndpoint(getProjectsEndpoint());

        projectPipe = pipeline.pipe(Project.class, config);

        config = new PipeConfig(url, Tag.class);
        config.setName(getTagsEndpoint());
        config.setEndpoint(getTagsEndpoint());

        tagPipe = pipeline.pipe(Tag.class, config);

        config = new PipeConfig(url, Task.class);
        config.setName(getTasksEndpoint());
        config.setEndpoint(getTasksEndpoint());

        taskPipe = pipeline.pipe(Task.class, config);

        Authenticator authenticator = new Authenticator(url);
        authModule = authenticator.auth("simple", new RestAuthenticationConfig());

        authToken = UUID.randomUUID().toString();
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public void login() throws IOException {

        final FutureResult<HeaderAndBody> future = new FutureResult<HeaderAndBody>();

        authModule.login("test", "tester", new Callback<HeaderAndBody>() {
            @Override
            public void onSuccess(HeaderAndBody data) {
                System.out.println("Login successful: " + data);
                future.setResult(data);
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println("Got exception: ");
                e.printStackTrace();
                future.setThrowable(e);
            }
        });

        try {
            future.get();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("login() error: ", e);
        }
    }

    @Override
    public List<Project> getProjects() throws IOException {

        final FutureResult<List<Project>> future = new FutureResult<List<Project>>();

        projectPipe.read(new Callback<List<Project>>() {
            @Override
            public void onSuccess(List<Project> data) {
                System.out.println("projectPipe read() successful: " + data);
                future.setResult(data);
            }

            @Override
            public void onFailure(Exception e) {
                future.setThrowable(e);
            }
        });

        try {
            return future.get();

        } catch (Exception e) {
            throw unpackException("getProjects() error:", e);
        }
    }

    @Override
    public List<Task> getTasks() throws IOException {

        final FutureResult<List<Task>> future = new FutureResult<List<Task>>();

        taskPipe.read(new Callback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> data) {
                System.out.println("taskPipe read() successful: " + data);
                future.setResult(data);
            }

            @Override
            public void onFailure(Exception e) {
                future.setThrowable(e);
            }
        });

        try {
            return future.get();

        } catch (Exception e) {
            throw unpackException("getTasks() error:", e);
        }
    }

    @Override
    public List<Tag> getTags() throws IOException {
        final FutureResult<List<Tag>> future = new FutureResult<List<Tag>>();

        tagPipe.read(new Callback<List<Tag>>() {
            @Override
            public void onSuccess(List<Tag> data) {
                System.out.println("tagPipe read() successful: " + data);
                future.setResult(data);
            }

            @Override
            public void onFailure(Exception e) {
                future.setThrowable(e);
            }
        });

        try {
            return future.get();

        } catch (Exception e) {
            throw unpackException("getTags() error:", e);
        }
    }

    private RuntimeException unpackException(String message, Exception e) {
        if (e instanceof ExecutionException) {
            ExecutionException ee = (ExecutionException) e;
            Throwable th = ee.getCause();
            if (th instanceof RuntimeException) {
                return (RuntimeException) th;
            }
        }
        return new RuntimeException(message, e);
    }
}
