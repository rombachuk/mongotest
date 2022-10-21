package com.rombachuk.mongotest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;


import org.apache.commons.configuration.ConfigurationException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public final class App {
    private App() {

    }

    private static Logger logger = LoggerFactory.getLogger(App.class.getName());
    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */

    public static void main(String[] args)  {

        System.setProperty("java.util.logging.SimpleFormatter.format",
              "[%1$tF %1$tT] [%4$-7s] %5$s %n");

        final String inifilename = "mongotest.ini";
        Configuration configuration = null;
        try {
        configuration = new Configuration(inifilename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        logger.info("Reading truststore info");
        String truststore_location = configuration.map.get("truststore").get("location").toString();
        String obftruststore_password = configuration.map.get("truststore").get("password").toString();
        byte[] decodedBytes = Base64.getDecoder().decode(obftruststore_password);
        String truststore_password = new String(decodedBytes);
        logger.info("Reading activity spec");
        String idle_time_text = configuration.map.get("activity").get("idle_time").toString();
        Integer idle_time = Integer.parseInt(configuration.map.get("activity").get("idle_time").toString());
        Integer loops = Integer.parseInt(configuration.map.get("activity").get("loops").toString());

        System.setProperty("javax.net.ssl.trustStore",truststore_location);
        System.setProperty("javax.net.ssl.trustStorePassword", truststore_password);

        logger.info("Making connection");

        final Connection connection = new Connection(configuration);

        logger.info("Running activity loops");

        for (Integer i = 0; i < loops; i++) {
            String istring = i.toString();
            
            try {
                logger.info("Sleeping for ["+idle_time_text+"] seconds...");
                TimeUnit.SECONDS.sleep(idle_time);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            List<Document> databases = connection.mongoClient.listDatabases().into(new ArrayList<>());
            databases.forEach(db -> logger.info("Loop ["+istring+"] "+db.toJson()));

        }

        connection.mongoClient.close();

        logger.info("Completed activity");
    }
}
