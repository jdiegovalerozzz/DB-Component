package com.library.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

    public class PoolManager {

        private final Queue<Cnn> connectionPool;
        private final Pool pool;
        private Properties sentences;

        public PoolManager(String configFilePath, String sentencesFilePath) throws SQLException {
            pool = new Pool(configFilePath);
            connectionPool = new LinkedList<>();
            loadSentences(sentencesFilePath);
            createPool();
        }

        public void createPool(){
            for (int i = 0; i < pool.getINITIAL_POOL_SIZE(); i++) {
                connectionPool.add(createConnection());
            }
        }

        public static synchronized PoolManager getPoolInstance(String configFilePath, String sentencesFilePath) throws SQLException {
            return new PoolManager(configFilePath, sentencesFilePath);
        }

        public synchronized Cnn getConnection() {
            try {
                while (true) {
                    for (Cnn cnn : connectionPool) {
                        if (cnn.getAvailable()) {
                            cnn.setAvailable(false);
                            return cnn;
                        }
                    }
                    if (pool.getCURRENT_POOL_SIZE() < pool.getMAX_POOL_SIZE()) {
                        Cnn newCnn = createConnection();
                        connectionPool.add(newCnn);
                        pool.incrementCURRENT_POOL_SIZE();
                    } else {
                        wait();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error getting connection", e);
            }
        }

        public synchronized Cnn getConnection(int connectionID){
            return getConnection();
        }

        public String getSentence(String queryID){
            return sentences.getProperty(queryID);
        }

        private void loadSentences(String filePath){
            sentences = new Properties();
            try (FileInputStream fis = new FileInputStream(filePath)){
                sentences.load(fis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public synchronized void returnConnection(Cnn cnn) {
            if (cnn != null) {
                cnn.setAvailable(true);
                notifyAll();
            }
        }

        private Cnn createConnection() {
            try {
                Cnn cnn = new Cnn();
                cnn.toConnect(pool.getURL(), pool.getUSER(), pool.getPASSWORD());
                return cnn;
            } catch (SQLException e) {
                throw new RuntimeException("Error creating connection", e);
            }
        }
    }


