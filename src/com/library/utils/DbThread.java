package com.library.utils;

import com.library.db.Cnn;
import com.library.db.PoolManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class DbThread extends Thread {
    private final PoolManager poolManager;
    private final static AtomicInteger successfulConnections = new AtomicInteger(0);
    private final static AtomicInteger failedConnections = new AtomicInteger(0);

    public static AtomicInteger getSuccessfulConnections() {
        return successfulConnections;
    }

    public static AtomicInteger getFailedConnections() {
        return failedConnections;
    }

    public DbThread(PoolManager poolManager) {
        this.poolManager = poolManager;
    }

    @Override
    public void run() {
        Cnn cnn = null;
        try {
            cnn = poolManager.getConnection();
            successfulConnections.incrementAndGet();
            ResultSet rs = cnn.executeQuery("SELECT * FROM peliculastabla WHERE id = 5");

            while (rs.next()) {
                int s1 = rs.getInt(1);
                String s2 = rs.getString(2);
                String s3 = rs.getString(3);
                System.out.println("-" + s1 + "\t" + "-" + s2 + "\t" + "-" + s3);
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
            failedConnections.incrementAndGet();
        } finally {
            if (cnn != null) {
                poolManager.returnConnection(cnn);
            }
        }
    }
}

