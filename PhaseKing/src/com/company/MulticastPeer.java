package com.company;

import java.net.*;
import java.io.*;
import java.util.Arrays;

public class MulticastPeer {
    public static void main(String args[]) throws IOException {
        //inicializa socket como nulo
        MulticastSocket s = null;
        //inicializa o vetor de processos e valores recebidos e controle 1
        String[] processos = {""};
        String[] valores = {""};
        //variaveis de controle (phase e round)
        int phase = 0;
        int round = 0;
        int p_ini = 0;
        int i_ini = 0;
        //verificar existencia de processos na rede, esperar ack de todos
        try {
            s = new MulticastSocket(6789);
            InetAddress group = InetAddress.getByName(args[1]);
            s.joinGroup(group);
            byte[] data = args[0].getBytes();
            DatagramPacket alfa = new DatagramPacket(data, data.length, group, 6789);
            s.send(alfa);
            byte[] buffer = new byte[1000];
            while (processos.length < 5) {
                DatagramPacket beta = new DatagramPacket(buffer, buffer.length);
                s.receive(beta);
                processos[i_ini] = Integer.toString(p_ini);
                valores[i_ini] = new String(beta.getData());
                p_ini++;
                i_ini++;
                System.out.println("Received:" + new String(beta.getData()));
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (s != null) s.close();
        }
    }

}
