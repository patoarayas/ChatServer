/*
 * Copyright (c) 2019. Patricio Araya - All Rights Reserved
 * See LICENSE.md for full license details.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ChatServer {

    /**
     * Attributes
     */
    private static final Logger log = LoggerFactory.getLogger(ChatServer.class);
    private static final int PORT = 9000;
    private static final List<ChatMessage> messages = new LinkedList<ChatMessage>();


    /**
     * Initialize chat server
     *
     * @throws : IOException
     */
    public static void main(String[] args) throws IOException {


        log.debug("Starting");

        // ServerSocket initialized
        final ServerSocket serverSocket = new ServerSocket(9000);
        log.debug("Server started in port {}, waiting for connections ..", 9000);

        while (true) {

            Socket clientSocket = null;

            try {

                clientSocket = serverSocket.accept();

                ConnectionHandler connection = new ConnectionHandler(clientSocket);
                Thread thread = new Thread(connection);
                log.debug("Connection on thread: {}", thread.getName());
                thread.start();
            } catch (Exception e) {

                log.error("Error", e);
                throw e;
            }
        }

    }

    /**
     * Generate an HTML with the messages embedded.
     *
     * @return : A String containing the HTML
     */
    static String generateHtml() {

        StringBuilder sb = new StringBuilder();
        File fl = new File("./src/web/chat.html");

        try (Scanner sc = new Scanner(fl)) {

            sc.useDelimiter("\\A");

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                sb.append(line);

                if (line.contains("chat-window")) {

                    for (ChatMessage cht : messages) {
                        sb.append("<p>");
                        sb.append(cht.toString());
                        sb.append("</p>");
                    }
                }
            }

        } catch (FileNotFoundException e) {
            log.error("Unable to retrieve html file");

        }

        return sb.toString();
    }

    /**
     * Add a new message to the list
     *
     * @param msg : The message to be added.
     */
    static void addMessage(ChatMessage msg) {

        synchronized (messages) {
            messages.add(msg);
        }
    }

    public static List<ChatMessage> getMessages() {
        return messages;
    }
}



