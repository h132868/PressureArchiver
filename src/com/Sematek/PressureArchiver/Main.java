package com.Sematek.PressureArchiver;

import java.util.Random;

class Main {

    public static void main(String[] args) {

        if(args.length == 0)
        {
            System.out.println("Proper Usage is: archiver.jar subscribeTopic publishTopic");
            System.out.println("Using default values, \"pressure/#\", \"debug/archiver\"");
            args = new String[2];
            args[0] = "pressure/#";
            args[1] = "debug/archiver";
        }
        
        Archiver archiver = new Archiver();
        Subscriber s = new Subscriber(archiver, args[0], args[1]
                + new Random().nextInt(100));
        s.connect();
    }
}