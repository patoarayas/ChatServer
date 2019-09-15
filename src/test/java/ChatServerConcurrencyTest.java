/*
 * Copyright (c) 2019. Patricio Araya - All Rights Reserved
 * See LICENSE.md for full license details.
 */

import jdk.jfr.Name;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;

@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatServerConcurrencyTest {


    private int testCount = 0;

    /**
     * Test if a message is added to ChatServer messages list
     *
     * @param num : Indicates the numeration of the ChatMessage
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @Order(1)
    void singleMessageShouldBeStored(int num) {

        ChatMessage msg = new ChatMessage("usr" + num, "msg" + num);
        ChatServer.addMessage(msg);
        testCount++;
        Assertions.assertEquals(ChatServer.getMessages().contains(msg), true,
                "ChatServer list messages should contain added message");

    }


    /**
     * Test if every message is added correctly to the list.
     */
    @Test
    @AfterEach
    synchronized void allConcurrentMessagesShouldHaveBeenStored() {

        List<ChatMessage> messages = ChatServer.getMessages();
        //System.out.println(messages.toString());
        Assertions.assertEquals(testCount, messages.size(), "Should have been <" + testCount + "> messages");
    }


}
