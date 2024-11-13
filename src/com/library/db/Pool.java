package com.library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

public class Pool {

        private int INITIAL_POOL_SIZE;
        private int MAX_POOL_SIZE;
        private int CURRENT_POOL_SIZE;
        private String URL, USER, PASSWORD;
        private LinkedList<Connection> pool;


        public Pool(String configFilePath) {
            pool = new LinkedList<>();
            loadConfig(configFilePath);

            createPool();
        }

        public void createPool(){
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                pool.add(createConnection());
                CURRENT_POOL_SIZE++;
            }
        }
        private Connection createConnection() {
            try {
                return DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                throw new RuntimeException("Error creando conexión", e);
            }
        }

        private void loadConfig(String filePath) {
            PropertiesHandler propertiesHandler = new PropertiesHandler(filePath);
            INITIAL_POOL_SIZE = Integer.parseInt(propertiesHandler.getProperty("initialConnections"));
            MAX_POOL_SIZE = Integer.parseInt(propertiesHandler.getProperty("maxConnections"));
            URL = propertiesHandler.getProperty("db.url");
            USER = propertiesHandler.getProperty("db.user");
            PASSWORD = propertiesHandler.getProperty("db.password");
        }


        public int getINITIAL_POOL_SIZE() {
            return INITIAL_POOL_SIZE;
        }

        public int getMAX_POOL_SIZE() {
            return MAX_POOL_SIZE;
        }

        public String getURL() {
            return URL;
        }

        public String getUSER() {
            return USER;
        }

        public String getPASSWORD() {
            return PASSWORD;
        }

        public synchronized int getCURRENT_POOL_SIZE() {
            return CURRENT_POOL_SIZE;
        }

        public synchronized void incrementCURRENT_POOL_SIZE() {
            if (CURRENT_POOL_SIZE < MAX_POOL_SIZE) {
                CURRENT_POOL_SIZE++;
            }
        }
    }


