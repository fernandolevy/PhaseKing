package com.company;
        import java.io.IOException;
        import java.util.Random;
public class process extends MulticastPeer{
    static int rand = new Random().nextInt(1);
    public static void main(String args[]) throws IOException {
        MulticastPeer.main(new String[] {Integer.toString(rand)+"/"+"228.5.6.7"+"/"+"0"});
        //acima, a sequencia de envio é v (valor randomico booleano), endereço de multicast, id de processo neste caso 0.
    }
}
