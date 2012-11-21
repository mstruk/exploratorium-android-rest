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
import org.aerogear.tests.dao.Project;
import org.aerogear.tests.dao.Tag;
import org.aerogear.tests.dao.Task;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class ClientUsingResteasyAPI extends AbstractHttpClient {
    @Override
    public List<Task> getTasks() throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Project> getProjects() throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Tag> getTags() throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void login() throws IOException {

        //ResteasyClient client = new ResteasyClient();
        //ResteasyWebTarget target = client.target("http://foo.com/resource");
    }

    @Override
    protected int getStatusCode() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String getStatusMessage() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String getResponseContentType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String getResponseErrorBody() throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
