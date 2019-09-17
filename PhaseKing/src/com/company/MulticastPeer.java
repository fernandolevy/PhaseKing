package com.company;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MulticastPeer {
    public static void main(String args[]) throws IOException {
        //inicializa socket como nulo
        MulticastSocket s = null;
        //inicializa o vetor de processos e valores recebidos e controle 1
        ArrayList<String> processos = new ArrayList<String>();
        ArrayList<String> valores = new ArrayList<String>();
        //variaveis de controle (phase e round)
        int phase = 0; //inicio de phase
        int round = 0;
        int n = 5;
        int valor_default = 0;
        int mult = 0;
        int process_id = -1;
        int f = 1; //processo malicioso
        //inicializar socket
        s = new MulticastSocket(6789);
        String[] saida = args[0].split("/");
        InetAddress group = InetAddress.getByName(saida[1]);
        //verificar existencia de processos na rede
        for (phase = 1; phase <= f + 1; phase++) {
            try {
                s.joinGroup(group);
                String nova_saida = saida[0] + "/" + saida[1] + "/" + saida[2];
                byte[] data = nova_saida.getBytes();
                DatagramPacket alfa = new DatagramPacket(data, data.length, group, 6789);
                s.send(alfa);
                process_id = Integer.parseInt(saida[2]);
                //guarda o id do processo que esta executando agora
                byte[] buffer;
                buffer = new byte[1000];
                while (processos.size() < 5) {
                    DatagramPacket beta = new DatagramPacket(buffer, buffer.length, group, 6789);
                    s.receive(beta);
                    String[] entrada = new String(beta.getData()).split("/");
                    boolean contains = processos.contains(entrada[2]);
                    //verifica se o processo recebido ja esta localizado no array
                    if (!contains) {
                        processos.add(entrada[2]);
                        valores.add(entrada[0]);
                        System.out.println("Received:" + String.valueOf(entrada[0]) + "---" + String.valueOf(entrada[2]));
                    }
                }
                System.out.println("PHASE 1 - ROUND 1 CONCLUIDO");
                //inicio do round 2 da primeira fase
                //contagem de 0 e 1
                int valores_1 = Collections.frequency(valores, "1");
                int valores_0 = Collections.frequency(valores, "0");
                String transmissao = "228.5.6.7";
                if (phase == process_id) {
                    //define o rei
                    byte[] buffer2;
                    buffer2 = new byte[1000];
                    if (valores_0 > valores_1) {
                        String opcao = 0+"/"+"/"+"/";
                        DatagramPacket gamma = new DatagramPacket(buffer2, buffer.length, group, 6789);
                        s.send(gamma);
                    } else if (valores_0 < valores_1) {
                        String opcao = 1+"/"+"/"+"/";
                        DatagramPacket gamma = new DatagramPacket(buffer2, buffer.length, group, 6789);
                        s.send(gamma);
                    } else if (valores_0 == valores_1) {
                        String opcao = valor_default+"/"+"/"+"/";
                        DatagramPacket gamma = new DatagramPacket(buffer2, buffer.length, group, 6789);
                        s.send(gamma);
                    }
                } else {
                    //wait
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

}
