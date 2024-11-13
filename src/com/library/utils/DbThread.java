package com.library.utils;

import com.library.db.DBComponent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbThread extends Thread {
    private final DBComponent dbComponent;
    private final String queryId;
    private final Object[] params;
    private ResultSet resultSet;
    private int updateResult;
    private boolean isSelect;
    private static int successfulConnections = 0;
    private static int failedConnections = 0;

    public DbThread(DBComponent dbComponent, String queryId, Object... params) {
        this.dbComponent = dbComponent;
        this.queryId = queryId;
        this.params = params;
        this.isSelect = queryId.toLowerCase().contains("select");
    }

    @Override
    public void run() {
        try {
            if (isSelect) {
                resultSet = dbComponent.executeQuery(queryId, params);
                if (resultSet.next()) {
                    printSelectResult(resultSet);
                } else {
                    System.out.printf("Thread %s: Parameter not found for query '%s'%n", Thread.currentThread().getName(), queryId);
                }
            } else {
                updateResult = dbComponent.executeUpdate(queryId, params);
                if (updateResult > 0) {
                    printUpdateResult(queryId, updateResult);
                } else {
                    System.out.printf("Thread %s: Parameter not found for query '%s'%n", Thread.currentThread().getName(), queryId);
                }
            }
            incrementSuccessfulConnections();
        } catch (SQLException e) {
            if ("Parameter not found".equals(e.getMessage())) {
                System.out.printf("Thread %s: %s for query '%s'%n", Thread.currentThread().getName(), e.getMessage(), queryId);
            } else {
                e.printStackTrace();
            }
            incrementFailedConnections();
        }
    }

    private void printSelectResult(ResultSet resultSet) throws SQLException {
        do {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String genre = resultSet.getString("genre");
            System.out.printf("Thread %s: ID = %d, Name = %s, Genre = %s%n", Thread.currentThread().getName(), id, name, genre);
        } while (resultSet.next());
    }

    private void printUpdateResult(String queryId, int updateResult) {
        System.out.printf("Thread %s: Query '%s' executed successfully, %d rows affected%n", Thread.currentThread().getName(), queryId, updateResult);
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public int getUpdateResult() {
        return updateResult;
    }

    public boolean isSelect() {
        return isSelect;
    }

    private static synchronized void incrementSuccessfulConnections() {
        successfulConnections++;
    }

    private static synchronized void incrementFailedConnections() {
        failedConnections++;
    }

    public static int getSuccessfulConnections() {
        return successfulConnections;
    }

    public static int getFailedConnections() {
        return failedConnections;
    }
}
