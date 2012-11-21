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

package org.aerogear.tests.server;

import org.aerogear.tests.dao.User;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class ApplicationContext {

    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<String, User>();

    private ConcurrentHashMap<String, String> authTokens = new ConcurrentHashMap<String, String>();

    private ConcurrentHashMap<String, Class> crudEndpoints = new ConcurrentHashMap<String, Class>();

    private ConcurrentHashMap<Class, List> db = new ConcurrentHashMap<Class, List>();

    public User getUser(String username) {
        return users.get(username);
    }

    public String getAuthToken(String username) {
        return authTokens.get(username);
    }

    public boolean checkAuthToken(String authToken) {
        // just scan authTokens
        for (String token : authTokens.values()) {
            if (token.equals(authToken)) {
                return true;
            }
        }
        return false;
    }

    public void addCRUD(String endpoint, Class clazz) {
        crudEndpoints.put(endpoint, clazz);
    }

    public Class getClassForEndpoint(String endpoint) {
        return crudEndpoints.get(endpoint);
    }

    public void enroll(User user) {
        if (user.getUsername() == null) {
            throw new IllegalArgumentException("username == null");
        }
        if (user.getPassword() == null) {
            throw new IllegalArgumentException("password == null");
        }

        if (user.getRoles() == null) {
            user.setRoles(new String[]{"admin"});
        }

        users.put(user.getUsername(), user);
    }

    public User login(String username, String password) {
        if (username == null) {
            return null;
        }

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            authTokens.put(username, UUID.randomUUID().toString());
            user.setLoggedIn(true);
            return user;
        }
        return null;
    }

    public <T> List<T> getEntitiesForType(Class<T> clazz) {
        List<T> ret = db.get(clazz);
        return ret != null ? ret: Collections.<T>emptyList();
    }

    public void addEntity(Object o) {
        List entities = db.get(o.getClass());
        if (entities == null) {
            entities = new LinkedList();
            db.put(o.getClass(), entities);
        }
        entities.add(o);
    }
}
