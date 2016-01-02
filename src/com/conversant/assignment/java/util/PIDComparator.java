package com.conversant.assignment.java.util;

import com.conversant.assignment.java.model.Message;
import java.util.Comparator;

/**
 * PIDComparator implements Comparator<Message> interface to compare Message objects,
 * so that Collection of Message can be sorted
 */
public class PIDComparator implements Comparator<Message> {

    /**
     * compare - compares PID of message1 and PID of message2 and returns result of comparison.
     *
     * @param  message1 - first Message object to be compared
     * @param  message2 - second Message object to be compared
     * @return -1 if message.getPID() < message2.getPID()
     *         0 if message.getPID() == message2.getPID()
     *         1 if message.getPID() > message2.getPID()
     *         These values are used in Collection.Sort() to sort Message collection based on PID.
     */

    @Override
    public int compare(Message message1, Message message2) {
        long pid1 = message1.getPid();
        long pid2 = message2.getPid();

        if(pid1 > pid2) return 1;
        else if(pid1 == pid2) return 0;
        else return -1;
    }
}
