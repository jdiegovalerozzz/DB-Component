package example;

import com.library.db.DBComponent;
import com.library.db.PoolManager;
import com.library.utils.DbThread;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
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

        // Initialize PoolManager with configuration files
        PoolManager poolManager = PoolManager.getInstance("src/config.properties", "src/sentences.properties");
        DBComponent dbComponent = new DBComponent(poolManager);

        // Retrieve the list of databases from the config.properties file
        String databases = poolManager.getDatabases();
        String[] dbIdentifiers = databases.split(",");  // Split the list into an array

        DbThread[] threads = new DbThread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            String queryId = "selectMovieById";
            String dbIdentifier = dbIdentifiers[i % dbIdentifiers.length];
            Object[] params = new Object[]{5};

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
}
