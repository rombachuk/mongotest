package com.rombachuk.mongotest;


import java.util.Base64;
import java.util.concurrent.TimeUnit;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


public class Connection {

    MongoClient mongoClient = null;

    private String ConnectionStringBuilder(Configuration configuration) {

        String connectionString = null;

        try {

        String username = configuration.map.get("credential").get("username").toString();      
        String obfpassword = configuration.map.get("credential").get("password").toString();
        byte[] decodedBytes = Base64.getDecoder().decode(obfpassword);
        String password = new String(decodedBytes);
        String database = configuration.map.get("target").get("database").toString();
        String server1hostport = configuration.map.get("target").get("server1hostport").toString();
        String server2hostport = configuration.map.get("target").get("server2hostport").toString();
        String server3hostport = configuration.map.get("target").get("server3hostport").toString();
        String authsource = configuration.map.get("target").get("authsource").toString();
        String replicaset = configuration.map.get("target").get("replicaset").toString();

        connectionString = String.format("mongodb://%s:%s@%s,%s,%s/%s?authsource=%s&replicaSet=%s",
                                    username,password,server1hostport,server2hostport,server3hostport,database,authsource,replicaset);
        return connectionString;
        }
        catch (NullPointerException e) {
            System.out.println("Invalid mongotest.ini - likely empty or missing values ["+e.toString()+"]");
            return connectionString;
        }
    }

    /**
     * @param configuration
     * 
     */
    public Connection (Configuration configuration) {

        try {

            Integer maxconnectionidletime = Integer.parseInt(configuration.map.get("connectionpool").get("maxconnectionidletime").toString());
            
            this.mongoClient = MongoClients.create(
            MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(ConnectionStringBuilder(configuration)))
            .applyToSslSettings(builder ->builder.enabled(true).invalidHostNameAllowed(true))
            .applyToConnectionPoolSettings(builder -> builder.maxConnectionIdleTime(maxconnectionidletime, TimeUnit.SECONDS))
            .build());                                                       
        }
        finally {
            assert true;
        }
    }
}
