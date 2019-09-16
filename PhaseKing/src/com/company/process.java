package com.company;
import java.util.Random;

public class process extends MulticastPeer{
    static int rand = new Random().nextInt(1);
    public static void main(String args[]) {
        MulticastPeer.main(new String[] {Integer.toString(rand), "228.5.6.7"});
        MulticastPeer.main(new String[] {"1", "228.5.6.7", "0"});
        MulticastPeer.main(new String[] {"2", "228.5.6.7", "1"});
        MulticastPeer.main(new String[] {"3", "228.5.6.7", "1"});
        MulticastPeer.main(new String[] {"4", "228.5.6.7", "0"});
        MulticastPeer.main(new String[] {"5", "228.5.6.7", "1"});

    }
}
