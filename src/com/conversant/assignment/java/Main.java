package com.conversant.assignment.java;

import com.conversant.assignment.java.concurrent.ThreadManager;
import com.conversant.assignment.java.model.Message;
import com.conversant.assignment.java.util.Constants;

import java.util.List;
import java.util.concurrent.Future;

public class Main {

    private final static int threadCount = 10;

    public static void main(String[] args) {

        /*
        workingDir is configurable parameter, so that in unit tests - files can be created in test directory and deleted at the end of the tests
        */
        String workingDir = System.getProperty("user.dir");
        // 1. spawn #<threadCount> children using executorService
        Future<Message>[] results = ThreadManager.generateChildThreads(threadCount, workingDir);

        // 2. main thread is waiting on completion of each child
        List<Message> messages = ThreadManager.getResultFromEachChild(results);

        // 3. write sorted report for each child
        ThreadManager.sortAndPrintMessages(messages);

        // 4. shutdown executorService and exit
        boolean shutdown = ThreadManager.shutDown();
        if(!shutdown) {
            System.err.println(Constants.SYSTEAM_FAILURE_MSG);
        }
        System.exit(0);
    }



}
