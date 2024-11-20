package com.library.utils;

import com.library.db.DBComponent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbThread extends Thread {
    private final DBComponent dbComponent;
    private final String dbIdentifier;
    private final String queryId;
    private final Object[] params;
    private ResultSet resultSet;
    private int updateResult;
    private boolean isSelect;
    private static int successfulConnections = 0;
    private static int failedConnections = 0;

    public DbThread(DBComponent dbComponent, String dbIdentifier, String queryId, Object... params) {
        this.dbComponent = dbComponent;
        this.dbIdentifier = dbIdentifier;
        this.queryId = queryId;
        this.params = params;
        this.isSelect = queryId.toLowerCase().contains("select");
    }

    @Override
    public void run() {
        try {
            if (isSelect) {

                resultSet = dbComponent.executeQuery(dbIdentifier, queryId, params);
                if (resultSet.next()) {
                    printSelectResult(resultSet); // Print results from SELECT query
                } else {
                    System.out.printf("Thread %s: No results found for query '%s'%n", Thread.currentThread().getName(), queryId);
                }
            } else {
                updateResult = dbComponent.executeUpdate(dbIdentifier, queryId, params);
                if (updateResult > 0) {
                    printUpdateResult(queryId, updateResult); // Print result for UPDATE/INSERT/DELETE query
                } else {
                    System.out.printf("Thread %s: No rows affected for query '%s'%n", Thread.currentThread().getName(), queryId);
                }
            }
            incrementSuccessfulConnections();
        } catch (SQLException e) {
            incrementFailedConnections();
            e.printStackTrace();
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
