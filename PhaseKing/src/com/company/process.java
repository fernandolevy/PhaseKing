package com.company;
        import java.util.Random;
public class process extends MulticastPeer{
    static int rand = new Random().nextInt(1);
    public static void main(String args[]) {
        MulticastPeer.main(new String[] {Integer.toString(rand), "228.5.6.7","0"});
    }
}
