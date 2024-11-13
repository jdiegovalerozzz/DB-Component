package example;

import com.library.db.DBComponent;
import com.library.db.PoolManager;
import com.library.utils.DbThread;

import javax.swing.*;
import java.sql.SQLException;

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

        try {

            PoolManager poolManager1 = PoolManager.getPoolInstance("src/config.properties", "src/sentences.properties");
            PoolManager poolManager2 = PoolManager.getPoolInstance("src/config.properties", "src/sentences.properties");

            DBComponent dbComponent1 = new DBComponent(poolManager1);
            DBComponent dbComponent2 = new DBComponent(poolManager2);

            DbThread[] threads = new DbThread[numberOfThreads];

            for (int i = 0; i < numberOfThreads; i++) {
                if (i < numberOfThreads / 2) {
                    if (i % 2 == 0) {
                        threads[i] = new DbThread(dbComponent1, "selectMovieById", 5);
                    } else {
                        threads[i] = new DbThread(dbComponent1, "selectMovieById", 6);
                    }
                } else {
                    if (i % 2 == 0) {
                        threads[i] = new DbThread(dbComponent2, "selectMovieById", 3);
                    } else {
                        threads[i] = new DbThread(dbComponent2, "selectMovieById",  7);
                    }
                }
                threads[i].start();
            }

            for (DbThread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("------------------------------------------------------------");
        System.out.println("Elapsed Time: " + (endTime - startTime) + "ms");
        System.out.println("------------------------------------------------------------");
        System.out.println("Successful Connections = " + DbThread.getSuccessfulConnections());
        System.out.println("------------------------------------------------------------");
        System.out.println("Failed Connections = " + DbThread.getFailedConnections());
        System.out.println("------------------------------------------------------------");
    }
}
