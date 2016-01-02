package com.conversant.assignment.java.concurrent;

import com.conversant.assignment.java.model.Message;
import com.conversant.assignment.java.model.Status;
import com.conversant.assignment.java.util.Constants;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * Child performs following tasks in call()
 *  1. creates a file where each filename is in the format of <pid>.<unix_epoch> in the current directory
 *  2. writes their pid to the file,
 *  3. writes the following string back to the parent via some form of inter-thread communication
 *
 *  Call() returns a result and may throw an exception.
    Note: The Callable interface is similar to Runnable, in that both are designed for classes whose instances are potentially executed by another thread.
          A Runnable, however, does not return a result and cannot throw a checked exception.
 *
 */
public class Child implements Callable<Message> {

    private FileOutputStream outputStream = null;
    private OutputStreamWriter fileContentWriter = null;
    private Status status = Status.FAILURE;

    /* workingDir is treated as configurable parameter, so that in unit tests - files can be created in test directory and deleted at the end of the tests.
    If no unit tests, then this should not be needed.
    */
    private String workingDir;
    public Child(String workingDir) {
        this.workingDir = workingDir;
    }
    /**
     * This method executes the task, this task can be called from other thread.
     * @return
     * @throws Exception
     */
    @Override
    public Message call() throws Exception {

        Message message = new Message(getPID());
        try {

            outputStream = new FileOutputStream(getFileName());
            fileContentWriter = new OutputStreamWriter(outputStream);
            fileContentWriter.write(String.valueOf(getPID()));
            status = Status.SUCCESS;
        } catch (FileNotFoundException e) {
            status = Status.FAILURE;
            System.err.printf(Constants.CHILD_THREAD_FAILURE_EMSG, getThreadName(), e.getMessage());
        } catch (IOException e) {
            status = Status.FAILURE;
            System.err.printf(Constants.CHILD_THREAD_FAILURE_EMSG,getThreadName(),e.getMessage());
        }
        finally {
            cleanUp();
            message.setStatus(status);
        }
        return message;
    }

    /**
     * Closes FileOutputStream and OutputStreamWriter
     */
    private void cleanUp() {
        if (fileContentWriter != null) {
            try {
                fileContentWriter.close();
            } catch (IOException e) {
                status = Status.FAILURE;
                System.err.printf(Constants.CHILD_THREAD_FAILURE_EMSG,getThreadName(),e.getMessage());
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                status = Status.FAILURE;
                System.err.printf(Constants.CHILD_THREAD_FAILURE_EMSG,getThreadName(),e.getMessage());
            }
        }
    }

    /**
     * Different threads generated in one process share the resources under the same process, hence they have the same Process ID, however
     * thread has unique identifier since when thread is created and it stays with thread until thread dies.
     *
     * @return long - returns unique identifier of this thread
     */
    private long getPID() {
        return Thread.currentThread().getId();
    }

    /**
     * @return String - returns thread name
     */
    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * @return long - returns filename in format <pid>.<unix_epoch>
     */
    private String getFileName() {
        StringBuffer fileName = new StringBuffer(workingDir);
        fileName.append("\\");
        fileName.append(getPID());
        fileName.append(".");
        fileName.append(System.currentTimeMillis()/1000L); // appending unix_epoch
        return fileName.toString();
    }

}