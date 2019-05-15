package com.Sematek.PressureArchiver;

import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("SpellCheckingInspection")
class LoginUtil {

    private static final String username = "pornkawy";
    private static final String password = "ELBK_4cc5nGY";
    private static final String clientId = "com.Sematek.PressureArchiver";
    private static final String URI = "tcp://m24.cloudmqtt.com:16821";
    private static final String dbName = "pressure";
    private static final String mongoConnURI = "mongodb://sematek-pressure-archiver:dr6bEhssuIObyycg@cluster0-shard-00-00-at0a5.mongodb.net:27017,cluster0-shard-00-01-at0a5.mongodb.net:27017,cluster0-shard-00-02-at0a5.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true";

    public static String getDbName() {
        return dbName;
    }
    public static String getMongoConnURI() {
        return mongoConnURI;
    }
    public static String getUsername() {
        return username;
    }
    public static String getPassword() {
        return password;
    }
    public static String getClientId() {
        return clientId;
    }
    public static URI getUri() throws URISyntaxException {
        return new URI (URI);
    }


}

