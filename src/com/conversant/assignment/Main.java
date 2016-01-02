package com.conversant.assignment;

import com.conversant.assignment.util.Constants;
import com.conversant.assignment.util.PIDComparator;
import com.conversant.assignment.concurrent.ThreadPool;
import com.conversant.assignment.model.Message;
import com.conversant.assignment.model.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {

    private final static int threadCount = 10;
    private final static long unknownPID = -1L; // this is used when Child fails to return PID back to Main Thread.

    public static void main(String[] args) {

        // 1. spawn #<threadCount> children using executorService
        Future<Message>[] results = ThreadPool.generateChildThreads(threadCount);

        // 2. main thread is waiting on completion of each child
        List<Message> messages = getResultFromEachChild(results);

        // 3. write sorted report for each child
        sortAndPrintMessages(messages);

        // 4. shutdown executorService and exit
        boolean shutdown = ThreadPool.shutDown();
        if(!shutdown) {
            System.err.println("System failed to complete execution of all threads in ExecutionService");
        }
        System.exit(0);
    }

    /**
     * A <tt>Future</tt> represents the result of an asynchronous computation.
     * get() method waits if necessary for the computation to complete, and then retrieves its result.
     * @param results - array of results of asynchronous threads/tasks.
     * @return List<Message> - returns list of messages from all threads/tasks, where message is data object shared between child thread and main thread.
     * This method handles - CancellationException, ExecutionException and InterruptedException thrown by get() on Future<Message>.
     */
    private static List<Message> getResultFromEachChild(Future<Message>[] results) {

        List<Message> messages = new ArrayList<Message>();

        for(int counter = 0; counter < threadCount; counter ++) {
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
    private static void sortAndPrintMessages(List<Message> messages) {

        Collections.sort(messages, new PIDComparator());

        for(Message message: messages) {
            System.out.println(message.toString());
        }
    }

}
