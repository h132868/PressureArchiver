package com.Sematek.PressureArchiver;

import java.util.Random;

class Main {

    public static void main(String[] args) {

        Archiver archiver = new Archiver();
        Subscriber s = new Subscriber(archiver, "pressure/#", "debug/archiver"
                + new Random().nextInt(100));
        s.connect();
    }
}