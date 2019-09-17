package com.company;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

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
        int flagReceive = 0;

        DatagramPacket messageOut;
        DatagramPacket messageIn;

        MulticastSocket s = null;
        InetAddress group = InetAddress.getByName(args[1]);
        s = new MulticastSocket(6789);
        s.joinGroup(group);

        for (int phase = 0; phase <= f; phase++) {

            System.out.println(String.valueOf(phase));
            // args give message contents and destination multicast group (e.g. "228.5.6.7")
            System.out.println("round 1");
            m = v.getBytes();
            messageOut = new DatagramPacket(m, m.length, group, 6789);
            s.send(messageOut);
            buffer = new byte[1000];
            for (int j = 0; j < n; j++) {        // get messages from others in group
                messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                System.out.println("Received round 1:" + new String(messageIn.getData()));
                if (Arrays.toString(messageIn.getData()).equals("1")) {
                    one = one + 1;
                } else {
                    zero = zero + 1;
                }
            }
            if (one > n / 2) {
                majority = 1;
                mult = one;
            } else if (zero > n / 2) {
                majority = 0;
                mult = zero;
            }

            System.out.println("round 2" + " " + String.valueOf(i) + " " + String.valueOf(phase));
            if (i == phase) {
                m = String.valueOf(majority).getBytes();
                messageOut = new DatagramPacket(m, m.length, group, 6789);
                s.send(messageOut);
                buffer = new byte[1000];
                for (int j = 0; j < n; j++) {        // get messages from others in group
                    messageIn = new DatagramPacket(buffer, buffer.length);
                    s.receive(messageIn);
                    System.out.println("Received round 2:" + new String(messageIn.getData()));
                    flagReceive++;
                }
            }else{
                for (int j = 0; j < n; j++) {        // get messages from others in group
                    messageIn = new DatagramPacket(buffer, buffer.length);
                    s.receive(messageIn);
                    System.out.println("Received round 2:" + new String(messageIn.getData()));
                    flagReceive++;
                }
            }
            tiebreaker = "0";

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
