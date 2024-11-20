package com.library.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBComponent {
    private final PoolManager poolManager;

    public DBComponent(PoolManager poolManager) {
        this.poolManager = poolManager;
    }

    // execute a SELECT query
    public ResultSet executeQuery(String dbIdentifier, String queryId, Object... params) throws SQLException {
        Pool pool = poolManager.getPool(dbIdentifier);
        Cnn cnn = pool.getCnn();
        try {
            String query = poolManager.getSentence(queryId);
            if (query == null) {
                throw new SQLException("Query not found: " + queryId);
            }
            return cnn.executeQuery(query, params);
        } finally {
            if (cnn != null) {
                pool.returnCnn(cnn);
            }
        }
    }

    // execute an UPDATE query
    public int executeUpdate(String dbIdentifier, String queryId, Object... params) throws SQLException {
        Pool pool = poolManager.getPool(dbIdentifier);
        Cnn cnn = pool.getCnn();
        try {
            String query = poolManager.getSentence(queryId);
            if (query == null) {
                throw new SQLException("Query not found: " + queryId);
            }
            return cnn.executeUpdate(query, params);
        } finally {
            if (cnn != null) {
                pool.returnCnn(cnn);
            }
        }
    }
}
