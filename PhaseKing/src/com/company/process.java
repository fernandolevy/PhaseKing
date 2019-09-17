package com.company;
        import java.io.IOException;
        import java.util.Random;
        import java.util.Scanner;

public class process extends MulticastPeer{
    static int rand = (int)(Math.random()*2);
    public static void main(String args[]) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite seu id:");
        String id = scanner.next();

        MulticastPeer.main(new String[] {Integer.toString(rand)+"/"+"228.5.6.7"+"/"+id});
        //acima, a sequencia de envio é v (valor randomico booleano), endereço de multicast, id de processo neste caso 0.
    }
}
