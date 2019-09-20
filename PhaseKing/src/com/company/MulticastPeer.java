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
        int n = 5;     //total de processos
        int valor_default = 0;
        int process_id = -1;
        int tiebreaker = -1;
        int f = 1;     //processo malicioso
        int turn = 0;
        int mult = 0;
        int novo_valor_envio = 0;
        String transmissao = "228.5.6.7";
        //inicializar socket
        s = new MulticastSocket(6789);
        String[] saida = args[0].split("/");
        InetAddress group = InetAddress.getByName(saida[1]);
        s.joinGroup(group);
        //verificar existencia de processos na rede
        for (phase = 1; phase <= f + 1; phase++) {
            try {
                System.out.println("PHASE" + phase + " - ROUND 1 INICIADO");
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
                System.out.println("PHASE" + phase + " - ROUND 1 CONCLUIDO");
                //inicio do round 2 da primeira fase
                //contagem de 0 e 1
                int valores_1 = Collections.frequency(valores, "1");
                //System.out.println(valores_1);
                int valores_0 = Collections.frequency(valores, "0");
                //System.out.println(valores_0);
                System.out.println("PHASE" + phase + " - ROUND 2 INICIADO");
                if (phase == process_id) {
                    //define o rei
                    //calcula os valores mais recebidos
                    //default_valor é 0, caso o numero de valores seja igual utiliza-se default (0)
                    if (valores_0 >= valores_1) {
                        String opcao = 0 + "/" + transmissao + "/" + process_id;
                        byte[] opcao2 = opcao.getBytes();
                        DatagramPacket gamma = new DatagramPacket(opcao2, opcao2.length, group, 6789);
                        s.send(gamma);
                    }
                    //se não for maior nem igual, é menor, portanto tem mais 1s do que 0s
                    else {
                        String opcao = 1 + "/" + transmissao + "/" + process_id;
                        byte[] opcao2 = opcao.getBytes();
                        DatagramPacket gamma = new DatagramPacket(opcao2, opcao2.length, group, 6789);
                        s.send(gamma);
                    }
                    //simula um peer que nao seja rei
                } else {
                    while (turn == 0) {
                        byte[] buffer3;
                        buffer3 = new byte[1000];
                        DatagramPacket delta = new DatagramPacket(buffer3, buffer3.length, group, 6789);
                        //tempo de espera
                        //s.setSoTimeout(1000);
                        try {
                            s.receive(delta);
                            String[] phaseador = new String(delta.getData()).split("/");
                            tiebreaker = Integer.parseInt(phaseador[0]);
                        } catch{
                            tiebreaker = valor_default;
                        }
                        //definição de mult fixo
                        if (valores_1 > valores_0) {
                            mult = 1;
                        }
                        //utiliza padrao caso o mult seja 0 tambem
                        else {
                            mult = 0;
                        }
                        //define v
                        if ((n / 2 + f)>mult) {
                            novo_valor_envio = 1;
                            String nova_saida3 = novo_valor_envio + "/" + transmissao + "/" + process_id;
                            byte[] data3 = nova_saida3.getBytes();
                            DatagramPacket eco = new DatagramPacket(data3, data3.length, group, 6789);
                            s.send(eco);
                        } else {
                            novo_valor_envio = tiebreaker;
                            String nova_saida3 = novo_valor_envio + "/" + transmissao + "/" + process_id;
                            byte[] data3 = nova_saida3.getBytes();
                            DatagramPacket eco = new DatagramPacket(data3, data3.length, group, 6789);
                            s.send(eco);
                        }
                        turn++;
                    }
                    turn = 0;//reset do turno para controle do phase king
                    if (phase == f + 1) {
                        System.out.println("Valor de decião final:" + novo_valor_envio);
                    }
                }
                System.out.println("PHASE" + phase + "- ROUND 2 CONCLUIDO");
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
