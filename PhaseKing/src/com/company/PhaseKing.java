package com.company;
//pega os dados do filme, converte para JSON e armazena em string

import java.util.ArrayList;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

import com.google.gson.Gson;

class Message {
    public String id;
    public String v;
}

public class PhaseKing {
    public static void main(String[] args) throws IOException {
        int i = Integer.parseInt(args[2]);
        String v = args[0];
        String tiebreaker = null;

        int one = 0;
        int majority = 0;
        int mult = 0;
        int zero = 0;
        byte[] m;
        byte[] buffer;

        int n = 5;
        int f = 1;
        DatagramPacket messageOut;
        DatagramPacket messageIn;

        Message message_json = new Message();
        Gson gson = new Gson();
        MulticastSocket s = null;
        InetAddress group = InetAddress.getByName(args[1]);

        for (int phase = 0; phase <= f; phase++) {
            s = new MulticastSocket(6789);
            s.joinGroup(group);
            message_json.id = args[2];
            message_json.v = v;
            String message_string_out = gson.toJson(message_json);
            m = message_string_out.getBytes();

            messageOut = new DatagramPacket(m, m.length, group, 6789);
            s.send(messageOut);

            buffer = new byte[18];
            while (n <= 5) {
                s.setSoTimeout(10000);
                for (int j = 0; j < n; j++) {
                    try {
                        // get messages from others in group
                        messageIn = new DatagramPacket(buffer, buffer.length);
                        s.receive(messageIn);
                        Message message_string_in = gson.fromJson(new String(messageIn.getData()), Message.class);
                        System.out.println("Received round 1 - " + "id:" + message_string_in.id + " v:" + message_string_in.v);

                        if (message_string_in.v.equals("1")) {
                            one = one + 1;
                        } else {
                            zero = zero + 1;
                        }
                    }catch (SocketTimeoutException e) {
                        // timeout exception.
                        System.out.println("Timeout reached!!! " + e);
                        n = n - 1;
                    }
                }
                break;
            }
            if (one > n / 2) {
                majority = 1;
                mult = one;
            } else if (zero > n / 2) {
                majority = 0;
                mult = zero;
            } else {
                majority = 1;
            }

            if (i == phase) {
                message_json.id = args[2];
                message_json.v = String.valueOf(majority);
                message_string_out = gson.toJson(message_json);
                m = message_string_out.getBytes();

                messageOut = new DatagramPacket(m, m.length, group, 6789);
                System.out.println("Received round 2 - Send Majority:" + new String(messageOut.getData()));
                s.send(messageOut);
                tiebreaker = v;

            } else {
                s.setSoTimeout(1000);   // set the timeout in millisecounds.
                while (true) {        // recieve data until timeout
                    try {
                        messageIn = new DatagramPacket(buffer, buffer.length);
                        s.receive(messageIn);
                        Message message_string_in = gson.fromJson(new String(messageIn.getData()), Message.class);

                        System.out.println("Received round 2 - Received Majority:" + "id:" + message_string_in.id + " v:" + message_string_in.v);
                        tiebreaker = message_string_in.v;
                    } catch (SocketTimeoutException e) {
                        // timeout exception.
                        System.out.println("Timeout reached!!! " + e);
                        s.close();
                        tiebreaker = "1";
                        break;
                    }
                }
            }
            if (mult > n / 2 + f) {
                v = String.valueOf(majority);
            } else {
                v = tiebreaker;
            }
            if (phase == f) {
                System.out.println("output decision value v:" + v);
            }
        }
    }
}
