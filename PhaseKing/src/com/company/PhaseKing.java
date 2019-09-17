package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Arrays;

public class PhaseKing {
    public static void main(String args[]) {
        int i = Integer.valueOf(args[2]);
        String v = args[0];
        String tiebreaker = null;
        int one = 0;
        int majority = 0;
        int mult = 0;
        int zero = 0;
        int n = 5;
        int f = Integer.valueOf(args[1]);
        for (int phase = 1; phase<= f+1; phase++){
            // args give message contents and destination multicast group (e.g. "228.5.6.7")
            MulticastSocket s =null;
            try {
                InetAddress group = InetAddress.getByName(args[1]);
                s = new MulticastSocket(6789);
                s.joinGroup(group);
                byte [] m = v.getBytes();
                DatagramPacket messageOut = new DatagramPacket(m, m.length, group, 6789);
                s.send(messageOut);
                byte[] buffer = new byte[1000];
                for(int j=0; j< n;j++) {		// get messages from others in group
                    DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                    s.receive(messageIn);
                    System.out.println("Received:" + new String(messageIn.getData()));
                    if (Arrays.toString(messageIn.getData()).equals("1")) {
                        one = one + 1;
                    }
                    else {
                        zero = zero + 1;
                    }
                }
                if (one > n/2) {
                    majority = 1;
                    mult = one;
                }
                else if (zero > n/2) {
                    majority = 0;
                    mult = zero;
                }
                if (i == phase) {
                    m = String.valueOf(majority).getBytes();
                    messageOut = new DatagramPacket(m, m.length, group, 6789);
                    s.send(messageOut);
                    buffer = new byte[1000];
                    for(int j=0; j< 5;j++) {		// get messages from others in group
                        DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                        s.receive(messageIn);
                        System.out.println("Received:" + new String(messageIn.getData()));
                    }
                    tiebreaker = v;
                    if (mult > n/2 + f) {
                        v = String.valueOf(majority);
                    }else{
                        v = tiebreaker;
                    }
                    if (phase == f + 1){
                        System.out.println("output decision value v:" + v);
                    }
                }
                s.leaveGroup(group);
            }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
            }catch (IOException e){System.out.println("IO: " + e.getMessage());
            }finally {if(s != null) s.close();}


        }
    }

}
