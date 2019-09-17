package com.company;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MulticastPeer {
    public static void main(String args[]) throws IOException {
        //inicializa socket como nulo
        MulticastSocket s = null;
        //inicializa o vetor de processos e valores recebidos e controle 1
        ArrayList<String> processos = new ArrayList<String>();
        ArrayList<String> valores = new ArrayList<String>();
        //variaveis de controle (phase e round)
        int phase = 0;
        int round = 0;
        //verificar existencia de processos na rede
        try {
            s = new MulticastSocket(6789);
            String[] saida = args[0].split("/");
            InetAddress group = InetAddress.getByName(saida[1]);
            s.joinGroup(group);
            String nova_saida = saida[0]+"/"+saida[1]+"/"+saida[2];
            byte[] data = nova_saida.getBytes();
            DatagramPacket alfa = new DatagramPacket(data, data.length, group, 6789);
            s.send(alfa);
            byte[] buffer;
            buffer = new byte[1000];
            while (processos.size() < 5) {
                DatagramPacket beta = new DatagramPacket(buffer, buffer.length,group,6789);
                s.receive(beta);
                String[] entrada = new String(beta.getData()).split("/");
                //System.out.println("Received:"+String.valueOf(entrada[2]));
                //System.out.println("Received:"+String.valueOf(entrada[1]));
                //System.out.println("Received:"+String.valueOf(entrada[0]));
                boolean contains = processos.contains(entrada[2]);
                //boolean contains = Arrays.asList(processos).contains(entrada[2]);
                //verifica se o processo recebido ja esta localizado no array
                if(!contains){
                    processos.add(entrada[2]);
                    valores.add(entrada[0]);
                    System.out.println("Received:" + String.valueOf(entrada[0])+"---"+String.valueOf(entrada[2]));
                }
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
