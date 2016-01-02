package com.conversant.assignment.java.concurrent;

import com.conversant.assignment.java.model.Message;
import com.conversant.assignment.java.model.Status;
import com.conversant.assignment.java.util.Constants;
import com.conversant.assignment.java.util.PIDComparator;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Helper class with methods which,
 *    1. creates pool of #<threadCount> threads using ExecutorService,
 *    2. does explicit and safe shutdown of ExecutorService,
 *    3. waits on result of each asynchronous task,
 *    4. sorts and prints records for each child on console of main.
 */
public class ThreadManager {

    private static ExecutorService executorService;
    private final static long unknownPID = -1L; // this is used when Child fails to return PID back to Main Thread.

    /**
     * generateChildThreads - creates pool of 10 threads using ExecutorService.
     * @param threadCount - number of threads to be generated and executed in parallel.
     * @return Future<Message>[] - A Future represents the result of an asynchronous computation.
     *                             Message is the data shared between child thread and main thread.
     *                             Future<Message>[] is the array of result of threads executed in parallel.
     */
    public static Future<Message>[] generateChildThreads(int threadCount, String workingDir) {

        final ThreadFactory customThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Child-%d")
                .setDaemon(true)
                .build();
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(threadCount);
        if(executorService == null) {
            executorService = new ThreadPoolExecutor(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS, queue, customThreadFactory);
        }

        Future<Message>[] results = new Future[threadCount];
        for(int counter = 0; counter < threadCount; counter ++) {
            results[counter] = executorService.submit(new Child(workingDir));
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
        } finally {
            executorService = null;
        }
        return done;
    }

    /**
     * A <tt>Future</tt> represents the result of an asynchronous computation.
     * get() method waits if necessary for the computation to complete, and then retrieves its result.
     * @param results - array of results of asynchronous threads/tasks.
     * @return List<Message> - returns list of messages from all threads/tasks, where message is data object shared between child thread and main thread.
     * This method handles - CancellationException, ExecutionException and InterruptedException thrown by get() on Future<Message>.
     */
    public static List<Message> getResultFromEachChild(Future<Message>[] results) {

        List<Message> messages = new ArrayList<Message>();

        for(int counter = 0; counter < results.length; counter ++) {
            Message message = new Message(unknownPID);
            try {
                message = results[counter].get(1, TimeUnit.SECONDS);
                if(!results[counter].isDone()) {
                    System.err.printf(Constants.TASK_INCOMPLETE_MSG, Thread.currentThread().getName());
                }
            } catch (InterruptedException e) {
                message.setStatus(Status.FAILURE);
                System.err.printf(Constants.GENERIC_FAILURE_MSG, e.getMessage());
            } catch (ExecutionException e) {
                message.setStatus(Status.FAILURE);
                System.err.printf(Constants.GENERIC_FAILURE_MSG, e.getMessage());
            } catch (CancellationException e) {
                message.setStatus(Status.FAILURE);
                System.err.printf(Constants.GENERIC_FAILURE_MSG, e.getMessage());
            } catch (Exception e) {
                message.setStatus(Status.FAILURE);
                System.err.printf(Constants.GENERIC_FAILURE_MSG, e.getMessage());
            } finally {
                messages.add(message);
            }
        }
        return messages;
    }

    /**
     * sorts the Collection of Message according to PID and displays messages on the console
     * @param messages - Collection of Message from all threads/tasks where Message is the data object shared between child thread and main thread
     */
    public static void sortAndPrintMessages(List<Message> messages) {

        Collections.sort(messages, new PIDComparator());

        for(Message message: messages) {
            System.out.println(message.toString());
        }
    }

}
