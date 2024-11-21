package example;

import com.library.db.DBComponent;
import com.library.db.PoolManager;
import com.library.utils.DbThread;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize PoolManager with configuration files
        PoolManager poolManager = PoolManager.getInstance("src/config.properties", "src/sentences.properties");
        DBComponent dbComponent = new DBComponent(poolManager);

        // Retrieve the list of databases from the config.properties file
        String databases = poolManager.getDatabases();
        String[] dbIdentifiers = databases.split(",");  // Split the list into an array

        // Prompt the user to choose between multi-threaded functionality and CRUD functionality
        String choice = JOptionPane.showInputDialog("Choose an option:\n1. Run multi-threaded functionality\n2. Enter CRUD mode");

        switch (choice) {
            case "1":
                runMultithreadedFunctionality(dbComponent, dbIdentifiers);
                break;

            case "2":
                runCrudFunctionality(dbComponent, dbIdentifiers);
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void runMultithreadedFunctionality(DBComponent dbComponent, String[] dbIdentifiers) {
        String input = JOptionPane.showInputDialog("Enter the number of threads");
        int numberOfThreads;

        try {
            numberOfThreads = Integer.parseInt(input);

            if (numberOfThreads <= 0) {
                throw new IllegalArgumentException("The number of threads must be positive");
            }
        } catch (NumberFormatException e) {
            System.out.println("Bad format. Please enter an integer");
            return;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        long startTime = System.currentTimeMillis();

        DbThread[] threads = new DbThread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            String queryId = "selectMovieById";
            String dbIdentifier = dbIdentifiers[i % dbIdentifiers.length];
            Object[] params = new Object[]{5}; // Example: Retrieve movie with ID = 5

            threads[i] = new DbThread(dbComponent, dbIdentifier, queryId, params);
            threads[i].start();
        }

        for (DbThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("------------------------------------------------------------");
        System.out.println("Elapsed Time: " + (endTime - startTime) + "ms");
        System.out.println("------------------------------------------------------------");
        System.out.println("Successful connections: " + DbThread.getSuccessfulConnections());
        System.out.println("------------------------------------------------------------");
        System.out.println("Failed connections: " + DbThread.getFailedConnections());
    }

    private static void runCrudFunctionality(DBComponent dbComponent, String[] dbIdentifiers) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Select a source database (1-" + dbIdentifiers.length + "):");
        for (int i = 0; i < dbIdentifiers.length; i++) {
            System.out.println((i + 1) + ". " + dbIdentifiers[i]);
        }

        int sourceDbChoice = scanner.nextInt();

        if (sourceDbChoice < 1 || sourceDbChoice > dbIdentifiers.length) {
            System.out.println("Invalid choice.");
            return;
        }
        String sourceDbIdentifier = dbIdentifiers[sourceDbChoice - 1];

        System.out.println("Select a target database (1-" + dbIdentifiers.length + "):");
        for (int i = 0; i < dbIdentifiers.length; i++) {
            System.out.println((i + 1) + ". " + dbIdentifiers[i]);
        }

        int targetDbChoice = scanner.nextInt();
        if (targetDbChoice < 1 || targetDbChoice > dbIdentifiers.length) {
            System.out.println("Invalid choice.");
            return;
        }
        String targetDbIdentifier = dbIdentifiers[targetDbChoice - 1];

        // Ask for the movie ID to copy
        System.out.print("Enter the movie ID to copy: ");
        int movieId = scanner.nextInt();

        try {
            // Select the movie from the source database
            String queryId = "selectMovieById" + sourceDbChoice; // Dynamically select based on db
            ResultSet resultSet = dbComponent.executeQuery(sourceDbIdentifier, queryId, movieId);

            if (resultSet.next()) {
                // If movie exists, print the details
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String genre = resultSet.getString("genre");

                System.out.printf("Selected movie: ID = %d, Name = %s, Genre = %s%n", id, name, genre);

                // Insert the movie into the target database
                String insertQueryId = "insertMovie" + targetDbChoice; // Dynamically insert into target DB
                dbComponent.executeUpdate(targetDbIdentifier, insertQueryId, id, name, genre);
                System.out.println("Movie copied successfully to target database.");
            } else {
                System.out.println("Movie not found in source database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
