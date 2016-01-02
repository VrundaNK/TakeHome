package com.conversant.assignment.concurrent;

import com.conversant.assignment.Child;
import com.conversant.assignment.model.Message;
import com.conversant.assignment.util.Constants;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * Helper class with methods which,
 *    1. create pool of #<threadCount> threads using ExecutorService,
 *    2. explicit and safe shutdown of ExecutorService
 */
public class ThreadPool {

    private static ExecutorService executorService;

    /**
     * generateChildThreads - creates pool of 10 threads using ExecutorService.
     * @param threadCount - number of threads to be generated and executed in parallel.
     * @return Future<Message>[] - A Future represents the result of an asynchronous computation.
     *                             Message is the data shared between child thread and main thread.
     *                             Future<Message>[] is the array of result of threads executed in parallel.
     */
    public static Future<Message>[] generateChildThreads(int threadCount) {

        final ThreadFactory customThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Child-%d")
                .setDaemon(true)
                .build();
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(threadCount);
        executorService = new ThreadPoolExecutor(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS,queue, customThreadFactory);

        Future<Message>[] results = new Future[threadCount];
        for(int counter = 0; counter < threadCount; counter ++) {
            results[counter] = executorService.submit(new Child());
        }
        return results;
    }

    /**
     * Explicit and safe shutdown.
     * @return boolean (true/false) - success/failure of completion of execution of each thread in ExecutionService.
    */
    public static boolean shutDown() {
        boolean done = false;
        try {
            if (executorService != null) {
                executorService.shutdown();
                done = executorService.awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch(InterruptedException e){
            System.err.printf(Constants.SHUTDOWN_FAILURE_MSG,e.getMessage());
        }
        return done;
    }

}
