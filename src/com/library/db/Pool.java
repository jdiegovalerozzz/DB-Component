package com.library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class Pool {
    private final String url;
    private final String user;
    private final String password;
    private final int initialPoolSize;
    private final int maxPoolSize;
    private int currentPoolSize;
    private final LinkedList<Cnn> pool;

    private Pool(String url, String user, String password, int initialPoolSize, int maxPoolSize) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.initialPoolSize = initialPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.pool = new LinkedList<>();
        createPool();
    }

    private void createPool() {
        for (int i = 0; i < initialPoolSize; i++) {
            pool.add(createCnn());
            currentPoolSize++;
        }
    }

    private Cnn createCnn() {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            return new Cnn(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating connection", e);
        }
    }

    public synchronized Cnn getCnn() {
        while (true) {
            for (Cnn cnn : pool) {
                if (cnn.getAvailable()) {
                    cnn.setAvailable(false);
                    return cnn;
                }
            }
            if (currentPoolSize < maxPoolSize) {
                incrementCurrentPoolSize();
                return createCnn();
            }
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void returnCnn(Cnn cnn) {
        if (cnn != null) {
            cnn.setAvailable(true);
            notifyAll(); // Notify waiting threads that a Cnn is now available
        }
    }

    public synchronized int getCurrentPoolSize() {
        return currentPoolSize;
    }

    public synchronized void incrementCurrentPoolSize() {
        if (currentPoolSize < maxPoolSize) {
            currentPoolSize++;
        }
    }

    public static synchronized Pool getInstance(String url, String user, String password, int initialPoolSize, int maxPoolSize) {
        return new Pool(url, user, password, initialPoolSize, maxPoolSize);
    }

}
