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

import com.google.gson.JsonObject;

import java.io.IOException;

import static org.aerogear.tests.util.Constants.APPLICATION_JSON;
import static org.aerogear.tests.util.Constants.PASSWORD;
import static org.aerogear.tests.util.Constants.USERNAME;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public abstract class AbstractHttpClient extends AbstractClient {

    protected abstract int getStatusCode() throws IOException;

    protected abstract String getStatusMessage() throws IOException;

    protected abstract String getResponseContentType();

    protected abstract String getResponseErrorBody() throws IOException;


    protected void checkErrors(String url) throws IOException {

        // error check response codes / statuses
        int status = getStatusCode();
        EndpointException ex = null;

        if (status == 401) {
            ex = new AuthenticationRequiredException("Endpoint requires authentication (401 Unauthorized): " + url);

        } else if (status >= 400) {
            ex = new EndpointException("HTTP Client error (" + status + " " + getStatusMessage() + "): " + url);

        } else if (status >= 500) {
            ex = new EndpointException("HTTP Server error (" + status + " " + getStatusMessage() + "): " + url);

        } else if (status != 200) {
            ex = new EndpointException("Server returned unexpected status (" + status + " " + getStatusMessage() + "): " + url);
        }

        if (ex != null) {
            throwEndpointException(ex);
        }

        // Sanity check
        String contentType = getResponseContentType();
        if (!APPLICATION_JSON.equals(contentType)) {
            throw new RuntimeException("Unexpected response Content-Type (not " + APPLICATION_JSON + "): " + contentType);
        }
    }


    protected void throwEndpointException(EndpointException ex) throws IOException {
        if (getStatusCode() >= 400) {
            ex.setServerMessage(getResponseErrorBody());
        }
        throw ex;
    }

    protected String composeAuthJSON() {
        JsonObject authInfo = new JsonObject();
        authInfo.addProperty(USERNAME, getUsername());
        authInfo.addProperty(PASSWORD, getPassword());
        return authInfo.toString();
    }

}
