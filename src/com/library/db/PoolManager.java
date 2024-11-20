package com.library.db;

import java.util.HashMap;
import java.util.Map;

public class PoolManager {
    private static PoolManager instance;
    private final Map<String, Pool> pools = new HashMap<>();
    private final PropertiesHandler propertiesHandler;
    private final Map<String, String> sentences = new HashMap<>();

    private PoolManager(String configFilePath, String sentencesFilePath) {
        propertiesHandler = new PropertiesHandler(configFilePath);
        loadDatabases();
        loadSentences(sentencesFilePath);
    }

    //Load databases
    private void loadDatabases() {
        int dbIndex = 1;
        while (true) {
            String dbUrl = propertiesHandler.getProperty("db" + dbIndex + ".url");
            if (dbUrl == null) break;

            String dbUser = propertiesHandler.getProperty("db" + dbIndex + ".user");
            String dbPassword = propertiesHandler.getProperty("db" + dbIndex + ".password");
            int initialConnections = Integer.parseInt(propertiesHandler.getProperty("db" + dbIndex + ".initialConnections"));
            int maxConnections = Integer.parseInt(propertiesHandler.getProperty("db" + dbIndex + ".maxConnections"));

            // Create a pool for this db and add it to the map
            Pool pool = Pool.getInstance(dbUrl, dbUser, dbPassword, initialConnections, maxConnections);
            pools.put("db" + dbIndex, pool);
            dbIndex++;
        }
    }

    private void loadSentences(String sentencesFilePath) {
        PropertiesHandler sentencesHandler = new PropertiesHandler(sentencesFilePath);
        for (String key : sentencesHandler.getProperties().stringPropertyNames()) {
            sentences.put(key, sentencesHandler.getProperty(key));
        }
    }

    public Pool getPool(String dbIdentifier) {
        return pools.get(dbIdentifier);
    }

    public String getSentence(String queryId) {
        return sentences.get(queryId);
    }

    public String getDatabases() {
        return propertiesHandler.getProperty("databases");
    }

    public static synchronized PoolManager getInstance(String configFilePath, String sentencesFilePath) {
        if (instance == null) {
            instance = new PoolManager(configFilePath, sentencesFilePath);
        }
        return instance;
    }
}
