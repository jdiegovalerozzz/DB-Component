package example;

import com.library.db.DBComponent;
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
            DBComponent db = new DBComponent("src/config.properties", "src/sentences.properties");
            DbThread[] threads = new DbThread[numberOfThreads];

            for (int i = 0; i < numberOfThreads; i++) {
                // Para este ejemplo, vamos a alternar entre una consulta SELECT y una UPDATE
                if (i % 2 == 0) {
                    threads[i] = new DbThread(db, "selectMovieById", 5); // Cambia los parámetros según tu necesidad
                } else {
                    threads[i] = new DbThread(db, "updateMovieGenreById", "Action", 5); // Cambia los parámetros según tu necesidad
                }
                threads[i].start();
            }

            for (DbThread thread : threads) {
                thread.join();
            }

        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
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
