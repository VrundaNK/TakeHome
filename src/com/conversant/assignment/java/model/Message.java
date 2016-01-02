package com.conversant.assignment.java.model;

/**
 * Represents the message (data object) shared between Main(Parent) and child thread.
 *
 * @field  pid  - unique ID representing child thread.
 * @field  status - status of child thread {SUCCESS, FAILURE}.
 */
public class Message {

    long pid;
    Status status;

    public Message(long pid) {

        this.pid = pid;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getPid() {
        return pid;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return getPid() + " " + getStatus();
    }
}
