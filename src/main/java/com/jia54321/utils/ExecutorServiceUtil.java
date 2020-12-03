/**
 * MIT License
 * 
 * Copyright (c) 2009-present GuoGang and other contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.jia54321.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * ExecutorServiceUtil服务
 *
 * @author gg
 * @create 2019-08-23
 */
public class ExecutorServiceUtil {

    /**
     * Number of idle threads to retain in a context's executor service.
     */
    public static final int CORE_POOL_SIZE = 0;
    /**
    // Apparently ScheduledThreadPoolExecutor has limitation where a task cannot be submitted from 
    // within a running task unless the pool has worker threads already available. ThreadPoolExecutor 
    // does not have this limitation.
    // This causes tests failures in SocketReceiverTest.testDispatchEventForEnabledLevel and
    // ServerSocketReceiverFunctionalTest.testLogEventFromClient.
    // We thus set a pool size > 0 for tests to pass.
     */
    public static final int SCHEDULED_EXECUTOR_POOL_SIZE = 2;

    
    /**
     * Maximum number of threads to allow in a context's executor service.
     *     // if you need a different MAX_POOL_SIZE, please file create a jira issue
     *     // asking to make MAX_POOL_SIZE a parameter.
     */
    public static final int MAX_POOL_SIZE = 32;
    
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {

        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = defaultFactory.newThread(r);
            if (!thread.isDaemon()) {
                thread.setDaemon(true);
            }
            thread.setName("jia54321-" + r.getClass().getSimpleName() + "-" + threadNumber.getAndIncrement());
            return thread;
        }
    };

    static public ScheduledExecutorService newScheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(SCHEDULED_EXECUTOR_POOL_SIZE, THREAD_FACTORY);
    }


    /**
     * Creates an executor service suitable for use by logback components.
     * @return executor service
     */
    static public ExecutorService newExecutorService() {
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
                        THREAD_FACTORY);
    }

    /**
     * Shuts down an executor service.
     * <p>
     * @param executorService the executor service to shut down
     */
    static public void shutdown(ExecutorService executorService) {
        executorService.shutdownNow();
    }
}
