package com.library.db;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

    public class PoolManager {

        private final Queue<Cnn> connectionPool;
        private final Pool pool;

        public PoolManager(String filePath) throws SQLException {
            pool = Pool.getInstance(filePath);
            connectionPool = new LinkedList<>();
            for (int i = 0; i < pool.getINITIAL_POOL_SIZE(); i++) {
                connectionPool.add(createConnection());
            }
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


