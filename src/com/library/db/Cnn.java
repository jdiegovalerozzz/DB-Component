package com.library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

    public class Cnn {

        private boolean available;
        private Connection connection;

        public void toConnect(String dbUrl, String user, String password) throws SQLException {
            connection = DriverManager.getConnection(dbUrl, user, password);
            available = true;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public boolean getAvailable() {
            return available;
        }

        public ResultSet executeQuery(String query) throws SQLException {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        }
    }


