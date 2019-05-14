package com.Sematek.PressureArchiver;

import com.mongodb.MongoSocketException;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.net.URI;
import java.net.URISyntaxException;

public class Subscriber implements MqttCallback, Runnable {

    private int sensorNumber;
    Archiver archiver;

    private final int qos = 1;
    private String topic;
    private MqttClient client;

    Subscriber(Archiver archiver, int sensorNumber) throws MqttException, URISyntaxException {
        this.sensorNumber = sensorNumber;
        this.archiver = archiver;
    }
    Subscriber(Archiver archiver) {
        this.archiver = archiver;
        this.topic = "pressure/archiver";

    }


    void connect() {


        try {
            URI uri = LoginUtil.getUri();
            this.topic = LoginUtil.getTopic(sensorNumber);
            String host = String.format("tcp://%s:%d", uri.getHost(), uri.getPort());

            this.client = new MqttClient(host, LoginUtil.getClientId(), new MemoryPersistence());
            MqttConnectOptions conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(true);
            conOpt.setUserName(LoginUtil.getUsername());
            conOpt.setPassword( LoginUtil.getPassword().toCharArray());
            conOpt.setConnectionTimeout(60);
            conOpt.setKeepAliveInterval(180);
            conOpt.setMaxInflight(16);
            conOpt.setAutomaticReconnect(true);
            System.out.println("Connecting to broker...");
            this.client.setCallback(this);
            this.client.connect(conOpt);
            System.out.println("Connected");

            System.out.println(conOpt.getConnectionTimeout());
            System.out.println(conOpt.getMaxReconnectDelay());
            System.out.println(conOpt.getKeepAliveInterval());

        } catch (MqttException e) {
            System.out.println("reason " + e.getReasonCode());
            System.out.println("msg " + e.getMessage());
            System.out.println("loc " + e.getLocalizedMessage());
            System.out.println("cause " + e.getCause());
            System.out.println("excep " + e);
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    private void disconnect() throws MqttException {
        client.disconnect();
    }

    public void publish(String payload) throws MqttException{
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        client.publish(topic,message);
    }


    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost because: " + cause);
        System.out.println("But we are not really going to do anything about that.");
        //System.exit(22);
    }

    public void subscribe (String topic) throws MqttException {
        client.subscribe(topic);
    }

    /**
     * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
     */
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
    /**
     * @see MqttCallback#messageArrived(String, MqttMessage)
     */
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        System.out.println("Subscriber->Recv: " + topic);
        archiver.archiveData(topic, message.toString());

    }

    @Override
    public void run() {
        connect();

    }
}

