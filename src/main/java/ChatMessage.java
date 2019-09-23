/*
 * Copyright (c) 2019. Patricio Araya - All Rights Reserved
 * See LICENSE.md for full license details.
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * This class represents a message on the chat.
 */
public class ChatMessage {

    private Date timeStamp;
    private String username;
    private String message;

    public ChatMessage(String username, String message) {

        this.timeStamp = new Date();
        this.username = username;
        this.message = message;
    }

    /**
     * Returns a  formated representation of the ChatMesagge data.
     *
     * @return : "[username]: [message]"
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(username).append(": ").append(message);

        return sb.toString();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
