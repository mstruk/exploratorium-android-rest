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

import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static org.aerogear.tests.util.Constants.APPLICATION_JSON;
import static org.aerogear.tests.util.Constants.AUTH_TOKEN;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class CRUDServlet extends HttpServlet {

    private ApplicationContext applicationContext;

    public CRUDServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // check authentication
        String authToken = req.getHeader(AUTH_TOKEN);
        if (authToken == null || !applicationContext.checkAuthToken(authToken)) {
            resp.setStatus(401);
            return;
        }

        Class clazz = applicationContext.getClassForEndpoint(req.getRequestURI());
        if (clazz == null) {
            resp.setStatus(404);
            return;
        }

        resp.setContentType(APPLICATION_JSON);
        List entities = applicationContext.getEntitiesForType(clazz);
        ObjectMapper mapper = new ObjectMapper();
        OutputStream os = resp.getOutputStream();
        mapper.writeValue(os, entities.toArray());
        os.close();
    }
}
