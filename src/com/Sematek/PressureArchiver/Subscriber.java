package com.Sematek.PressureArchiver;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.net.URI;
import java.net.URISyntaxException;

class Subscriber implements MqttCallbackExtended {

    private final Archiver archiver;
    private final String subscribeTopic;
    private final String publishTopic;
    private MqttClient client;

    Subscriber(Archiver archiver, String subscribeTopic, String publishTopic) {
        this.archiver = archiver;
        this.subscribeTopic = subscribeTopic;
        this.publishTopic = publishTopic;

    }
    void connect() {
        try {
            URI uri = LoginUtil.getUri();
            String host = String.format("tcp://%s:%d", uri.getHost(), uri.getPort());

            this.client = new MqttClient(host, LoginUtil.getClientId(), new MemoryPersistence());
            MqttConnectOptions conOpt = new MqttConnectOptions();
            conOpt.setCleanSession(false);
            conOpt.setUserName(LoginUtil.getUsername());
            conOpt.setPassword(LoginUtil.getPassword().toCharArray());
            conOpt.setConnectionTimeout(60);
            conOpt.setKeepAliveInterval(180);
            conOpt.setMaxInflight(16);
            conOpt.setAutomaticReconnect(true);
            System.out.println("Connecting to broker...");
            this.client.setCallback(this);
            this.client.connect(conOpt);

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

    private void publish(String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        final int qos = 1;
        message.setQos(qos);
        client.publish(publishTopic, message);
    }


    /**
     * @see MqttCallback#connectionLost(Throwable)
     */
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost because: " + cause);
        System.exit(22);
    }

    private void subscribe(String topic) throws MqttException {
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
    public void messageArrived(String topic, MqttMessage message) {

        System.out.println("Subscriber->Recv: " + topic);
        archiver.archiveData(topic, message.toString());

    }

    @Override
    public void connectComplete(boolean reconnect, java.lang.String serverURI){
        if(reconnect) {
            try {
                System.out.println("Re-connected!");
                publish("reconnected!");
                Thread.sleep(1000);
                this.client.subscribe(subscribeTopic);
            } catch (MqttException | InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            subscribe(subscribeTopic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

