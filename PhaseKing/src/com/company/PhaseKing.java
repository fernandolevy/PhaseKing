package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class PhaseKing {
    public static void main(String args[]) {
        //args que passam 0-é o v, 1->endereço da maks, 2->id da tarefa
        String i = args[0];
        String v = args[3];
        int one = 0;
        int majority;
        int mult = 0;
        int two = 0;
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
                for(int j=0; j< 5;j++) {		// get messages from others in group
                    DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                    s.receive(messageIn);
                    System.out.println("Received:" + new String(messageIn.getData()));
                    if (Integer.valueOf(String.valueOf(messageIn.getData())) == 1) {
                        one = one + 1;
                    }
                    else {
                        two = two + 1;
                    }
                }
                if (one > 5/2) {
                    majority = 1;
                    mult = one;
                }
                else if (two > 5/2) {
                    majority = 2;
                    mult = two;
                }
                s.leaveGroup(group);
            }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
            }catch (IOException e){System.out.println("IO: " + e.getMessage());
            }finally {if(s != null) s.close();}
//                (1g) Execute the following Round 2 actions: // actions in round two of each phase
//                (1h) if i = phase then // only the phase leader executes this send step
//                        (1i) broadcast majority to all processes;
//                (1j) receive tiebreaker from Pphase (default value if nothing is received);
//                (1k) if mult > n/2 + f then
//                        (1l) v ←− majority;
//                (1m) else v ←− tiebreaker;
//                (1n) if phase = f + 1 then
//                        (1o) output decision value v.
        }
    }

}
