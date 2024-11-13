package com.library.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBComponent {
    private final PoolManager poolManager;

    public DBComponent(String configFilePath, String sentencesFilePath) throws SQLException {
        this.poolManager = PoolManager.getPoolInstance(configFilePath, sentencesFilePath);
    }

    public ResultSet executeQuery(String queryId, Object... params) throws SQLException {
        final Cnn cnn = poolManager.getConnection();
        try {
            String query = poolManager.getSentence(queryId);
            if (query == null) {
                throw new SQLException("Query not found: " + queryId);
            }
            ResultSet resultSet = cnn.executeQuery(query, params);
            if (!resultSet.isBeforeFirst()) { // Verifica si el ResultSet está vacío
                throw new SQLException("Parameter not found");
            }
            return resultSet;
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
            int result = cnn.executeUpdate(query, params);
            if (result == 0) { // Verifica si no se afectaron filas
                throw new SQLException("Parameter not found");
            }
            return result;
        } finally {
            if (cnn != null && cnn.getConnection() != null) {
                poolManager.returnConnection(cnn);
            }
        }
    }
}
