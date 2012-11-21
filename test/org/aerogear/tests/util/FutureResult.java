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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com">Marko Strukelj</a>
 */
public class FutureResult<T> implements Future<T> {

    private Throwable throwable;
    private boolean done;
    private T result;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        synchronized (this) {
            if (!done) {
                this.wait();
            }
        }

        if (throwable != null) {
            throw new ExecutionException(throwable);
        }

        return result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (this) {
            if (!done) {
                unit.timedWait(this, timeout);
            }
        }

        if (throwable != null) {
            throw new ExecutionException(throwable);
        }

        return result;
    }

    public void setThrowable(Throwable throwable) {
        synchronized (this) {
            done = true;
            this.throwable = throwable;
            this.notify();
        }
    }

    public void setResult(T result) {
        synchronized (this) {
            done = true;
            this.result = result;
            this.notify();
        }
    }
}
