package com.Sematek.PressureArchiver;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Main {

    public static void main(String[] args) throws MqttException {

        Archiver archiver = new Archiver();
        Subscriber s = new Subscriber(archiver);
        s.connect();
        s.subscribe("pressure/#");
    }

}