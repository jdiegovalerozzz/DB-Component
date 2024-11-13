package example;

import com.library.utils.DbThread;
import com.library.db.PoolManager;
import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String input = JOptionPane.showInputDialog("Enter the number of threads");
        int numberOfThreads;

        try{
            numberOfThreads = Integer.parseInt(input);

            if (numberOfThreads <= 0){
                throw new IllegalArgumentException("The number of threads must be positive");
            }
        }catch (NumberFormatException e){
            System.out.println("Bad format. Please enter an integer");
            return;
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            PoolManager poolManager = new PoolManager("src/config.properties");
            DbThread[] threads = new DbThread[numberOfThreads];

            for (int i = 0; i < numberOfThreads; i++) {
                threads[i] = new DbThread(poolManager);
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
