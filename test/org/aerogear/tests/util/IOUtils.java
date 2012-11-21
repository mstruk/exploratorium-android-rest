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

package org.aerogear.tests.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class IOUtils {
    public static void copyAndCloseStream(Reader in, Writer out) throws IOException {
        char[] buf = new char[8192];
        int rc = -1;
        try {
            rc = in.read(buf);
            while (rc != -1) {
                if (out != null) {
                    out.write(buf, 0, rc);
                }
                rc = in.read(buf);
            }
        } finally {
            try {
                in.close();
            } catch (Exception ignored) {
            }

            if (out != null) {
                out.close();
            }
        }
    }

    public static void copyAndCloseStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int rc = -1;
        try {
            rc = in.read(buf);
            while (rc != -1) {
                if (out != null) {
                    out.write(buf, 0, rc);
                }
                rc = in.read(buf);
            }
        } finally {
            try {
                in.close();
            } catch (Exception ignored) {
            }

            if (out != null) {
                out.close();
            }
        }
    }
}
