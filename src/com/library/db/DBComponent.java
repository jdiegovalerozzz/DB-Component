package com.library.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBComponent {
    private final PoolManager poolManager;

    public DBComponent(PoolManager poolManager) {
        this.poolManager = poolManager;
    }

    public ResultSet executeQuery(String queryId, Object... params) throws SQLException {
        final Cnn cnn = poolManager.getConnection();
        try {
            String query = poolManager.getSentence(queryId);
            if (query == null) {
                throw new SQLException("Query not found: " + queryId);
            }
            return cnn.executeQuery(query, params);
        } finally {
            if (cnn != null && cnn.getConnection() != null) {
                poolManager.returnConnection(cnn);
            }
        }
    }

    public int executeUpdate(String queryId, Object... params) throws SQLException {
        final Cnn cnn = poolManager.getConnection();
        try {
            String query = poolManager.getSentence(queryId);
            if (query == null) {
                throw new SQLException("Query not found: " + queryId);
            }
            return cnn.executeUpdate(query, params);
        } finally {
            if (cnn != null && cnn.getConnection() != null) {
                poolManager.returnConnection(cnn);
            }
        }
    }
}
