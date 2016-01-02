package com.conversant.assignment.test;

import com.conversant.assignment.java.concurrent.ThreadManager;
import com.conversant.assignment.java.model.Message;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

public class ThreadManagerTest {

    String workingDir;
    @Before
    public void setUp() {
        StringBuilder dir = new StringBuilder(System.getProperty("user.dir"));
        dir.append("\\src\\test\\data\\");
        workingDir = dir.toString();
        (new File(workingDir)).mkdir();
    }

    @Test
    public void generateChildThreadsTest() {

        int threadCount = 10;
        Future<Message>[] results = ThreadManager.generateChildThreads(threadCount, workingDir);
        Assert.assertEquals(threadCount, results.length);

    }

    @Test
    public void getResultFromEachChildTest() {

        int threadCount = 10;
        List<Message> messages = ThreadManager.getResultFromEachChild(ThreadManager.generateChildThreads(threadCount, workingDir));
        Assert.assertEquals(threadCount, messages.size());

        File[] files = (new File(workingDir)).listFiles();
        Assert.assertEquals(threadCount, files.length);
    }

    @After
    public void tearDown() {
        ThreadManager.shutDown();
        File files[] = (new File(workingDir)).listFiles();
        for (File f: files) {
            f.delete();
        }
    }

}