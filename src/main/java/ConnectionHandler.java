
/*
 * Copyright (c) 2019. Patricio Araya - All Rights Reserved
 * See LICENSE.md for full license details.
 */

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.*;

import java.net.Socket;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Connection Handler
 * Handle a http 1.1 request on a socket.
 */
public class ConnectionHandler implements Runnable {


    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
    private Socket socket;

    /**
     * Constructor
     *
     * @param socket A client socket
     */
    ConnectionHandler(Socket socket) {

        this.socket = socket;

    }

    /**
     * This method is executed when a new thread is started.
     * Log status and call method ProcessConnection()
     */
    @Override
    public void run() {

        log.debug("========================================================================================");
        log.debug("Connection stablished from {} in port {}.", socket.getInetAddress().getHostAddress(), socket.getPort());

        try {
            processConnection();
        } catch (IOException e) {
            log.error("Error at Process Conection");
            e.printStackTrace();
        }


    }

    /**
     * Process the connection.
     * Read Inputstream, log request info and write to OutputStream
     */
    private void processConnection() throws IOException {

        log.debug("Processing connection...");

        final List<String> lines = readInputStream();

        String request = lines.get(0);
        log.debug("Request: {}", request);

        final PrintWriter pw = new PrintWriter(socket.getOutputStream());

        if (request.contains("GET")) {
            // Process GET request

            pw.println("HTTP/1.1 200 OK");
            pw.println("Server: DSM-CHAT v0.0.1");
            pw.println("Content-Type: text/html; charset=UTF-8");
            pw.println();
            pw.println(ChatServer.generateHtml());
            pw.println();
            pw.flush();

        } else if (request.contains("POST")) {
            // Process POST request

            ChatMessage chatMessage = generateMessage(lines);

            if (chatMessage != null) {

                ChatServer.addMessage(chatMessage);

                log.info("At {} user {} said {} from {}.",
                        chatMessage.getTimeStamp(), chatMessage.getUsername(),
                        chatMessage.getMessage(), socket.getInetAddress());

            }


            pw.println("HTTP/1.1 200 OK");
            pw.println("Server: DSM-CHAT v0.0.1");
            pw.println("Content-Type: text/html; charset=UTF-8");
            pw.println();

            pw.println(ChatServer.generateHtml());
            pw.println();
            pw.flush();


        } else {
            // Send a error response

            pw.println("HTTP/1.1 400 ERROR");
            pw.println("Server: DSM-CHAT v0.0.1");
            pw.println();
            pw.flush();

        }


        log.debug("Connection process ended.");

        socket.close();

    }

    /**
     * Read this connection InputStream
     *
     * @return : The Strings readed.
     */
    private List<String> readInputStream() throws IOException {

        InputStream is = socket.getInputStream();
        List<String> input = new ArrayList<>();

        BufferedReader bf = new BufferedReader(new InputStreamReader(is));


        while (true) {

            String line = bf.readLine();

            if (line.length() == 0) {
                // EOF

                // Get the body content length
                int contentLength = 0;
                for (String str : input) {
                    if (str.contains("Content-Length:")) {
                        contentLength = Integer.parseInt(str.substring(16));
                    }
                }
                if (contentLength != 0) {
                    // The InputStream contains a body

                    // Read the body content as a char, then add to the StringBuilder
                    StringBuilder sb = new StringBuilder(contentLength);
                    for (int i = 0; i < contentLength; i++) {
                        sb.appendCodePoint(bf.read());
                    }

                    // Decode the body from URL encodint into UTF-8, then add to the input list
                    String requestBody = URLDecoder.decode(sb.toString(), StandardCharsets.UTF_8);
                    input.add(requestBody);
                    break;

                } else {
                    // The EOF have been reached and it doesn't have a body
                    break;
                }

            } else {
                // The EOF have not been reached yet.
                input.add(line);
            }
        }

        if (input.isEmpty()) {
            input.add("ERROR");
        }
        return input;

    }

    /**
     * Parse an input stream and extract the username and message to be added
     *
     * @param input : The request InputStream
     * @return : An ChatMessage instance
     */
    private ChatMessage generateMessage(List<String> input) {

        if (input.isEmpty()) {
            return null;
        }

        String bodyContent = input.get(input.size() - 1);
        bodyContent = bodyContent.replace("username=", "");
        bodyContent = bodyContent.replace("message=", "");

        String username = bodyContent.substring(0, bodyContent.indexOf('&'));
        String message = bodyContent.substring(bodyContent.indexOf('&') + 1, bodyContent.length());

        if (username.isEmpty() || message.isEmpty()) {
            return null;
        }

        ChatMessage newMessage = new ChatMessage(username, message);

        return newMessage;

    }
}




