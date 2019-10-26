package com.company;
//pega os dados do filme, converte para JSON e armazena em string

import java.util.ArrayList;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

import com.google.gson.Gson;

//Classe que representa a mensagem
class Message {
    //id do processo
    public String id;
    //Valor do processo
    public String v;
    //Rei do processo 0 para false e 1 para true
    public int king;
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
        int turno = 0;
        int n = 5;
        int f = 1;

        DatagramPacket messageOut;
        DatagramPacket messageIn;

        Message message_json = new Message();
        Gson gson = new Gson();
        MulticastSocket s = null;
        InetAddress group = InetAddress.getByName(args[1]);
        s = new MulticastSocket(6789);

        //Entra no grupo
        s.joinGroup(group);
        try {
            //Espera para que os 5 processos possam sincronizar
            Thread.sleep(10000);

            //For que intera a fase 1 e 2
            for (int phase = 0; phase <= f; phase++) {
                System.out.println("\n");
                System.out.println("Phase: " + String.valueOf(phase));
                //Salva o id na classe message
                message_json.id = args[2];
                //Salva o v na classe message
                message_json.v = v;
                //Salva o rei na classe message
                message_json.king = 0;
                //Converte a classe em json e depois em string
                String message_string_out = gson.toJson(message_json);
                System.out.println("Send round 1 - " + message_string_out);
                m = message_string_out.getBytes();
                messageOut = new DatagramPacket(m, m.length, group, 6789);
                //Envia a mensagem
                s.send(messageOut);

                buffer = new byte[27];
                //Loop que corresponde aos 5 processos
                while (n <= 5) {
                    //Espera durante 5 segundos a respostas do grupo
                    s.setSoTimeout(5000);
                    //Intera o numero de processos n para receber as mensagem
                    for (int j = 0; j < n; j++) {
                        //Tenta receber as mensagem
                        try {
                            // get messages from others in group
                            messageIn = new DatagramPacket(buffer, buffer.length);
                            //recebe a mensagem
                            s.receive(messageIn);
                            Message message_string_in = gson.fromJson(new String(messageIn.getData()), Message.class);
                            System.out.println("Received round 1 - " + "id:" + message_string_in.id + " v:" + message_string_in.v);

                            //Intera os valores 1
                            if (message_string_in.v.equals("1")) {
                                one = one + 1;
                            }
                            //Intera os valores 0
                            else {
                                zero = zero + 1;
                            }
                        }
                        //Caso o tempo de 5 segundos passe e nada seja recebido
                        catch (SocketTimeoutException e) {
                            // timeout exception.
                            System.out.println("Timeout reached!!! " + e);
                            //O numero de processos é subtraido
                            n = n - 1;
                        }
                    }
                    break;
                }
                //Computa a maioria do valor 1
                if (one > n / 2) {
                    majority = 1;
                    mult = one;
                }
                //Computa a maioria do valor 0
                else if (zero > n / 2) {
                    majority = 0;
                    mult = zero;
                }
                //Caso não aja maioria escolhe valor default
                else {
                    majority = 1;
                    System.out.println("Empate valor default:" + majority);
                }

                //Caso o processo seja rei
                if (i == phase) {
                    message_json.id = args[2];
                    message_json.v = String.valueOf(majority);
                    message_json.king = 1;
                    //Converte a classe em json e salva na string
                    message_string_out = gson.toJson(message_json);
                    m = message_string_out.getBytes();
                    messageOut = new DatagramPacket(m, m.length, group, 6789);
                    System.out.println("Send round 2 - Send Majority:" + new String(messageOut.getData()));
                    //Envia a mesangem
                    s.send(messageOut);
                    //Salva como tiebreaker o seu próprio marjority
                    tiebreaker = v;

                }
                //Caso o processo não seja rei
                else {
                    //Espera 25 segundos
                    s.setSoTimeout(25000);   // set the timeout in millisecounds.
                    //Looping infinito
                    while (true) {        // recieve data until timeout
                        //Tenta receber a mensagem
                        try {
                            messageIn = new DatagramPacket(buffer, buffer.length);
                            s.receive(messageIn);
                            Message message_string_in = gson.fromJson(new String(messageIn.getData()), Message.class);
                            //Se a mensagem recebida for do rei
                            if (message_string_in.king == 1) {
                                System.out.println("Received round 2 - Received Majority:" + "id:" + new String(messageIn.getData()));
                                tiebreaker = message_string_in.v;
                                break;
                            }

                        }
                        //Caso passe 25 segundos e não tenha recebido a mensagem
                        catch (SocketTimeoutException e) {
                            // timeout exception.
                            System.out.println("SetTimeOut Nada recebido valor default: 1!!! " + e);
                            //Salva o valor do tiebreaker com um valor default
                            tiebreaker = "1";
                            break;
                        }
                    }
                }
                //Caso a maioria do processo seja maior que a metade mais a tolerancia a falha
                if (mult > n / 2 + f) {
                    //Salva o valor da maioria do proprio processo
                    v = String.valueOf(majority);
                    System.out.println("Majority v:" + v);
                    message_json.id = args[2];
                    message_json.v = v;
                    message_json.king = 1;
                    //Converte a classe em json e salva na string
                    message_string_out = gson.toJson(message_json);
                    m = message_string_out.getBytes();
                    messageOut = new DatagramPacket(m, m.length, group, 6789);
                    System.out.println("Send round 2:" + new String(messageOut.getData()));
                    //Envia a mesangem
                    s.send(messageOut);
                }
                //Caso o mult do processo não garanta maioria mais a falha e envia para o processo round 2
                else {
                    //Salva o tiebraker recebido do rei
                    v = tiebreaker;
                    System.out.println("Tiebreaker v:" + v);
                    message_json.id = v;
                    message_json.v = String.valueOf(majority);
                    message_json.king = 1;
                    //Converte a classe em json e salva na string
                    message_string_out = gson.toJson(message_json);
                    m = message_string_out.getBytes();
                    messageOut = new DatagramPacket(m, m.length, group, 6789);
                    System.out.println("Send round 2:" + new String(messageOut.getData()));
                    //Envia a mesangem
                    s.send(messageOut);

                }
                //Caso seja a ultima fase do processo
                if (phase == f) {
                    //Printa a decisão do consenso
                    System.out.println("output decision value v:" + v);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
