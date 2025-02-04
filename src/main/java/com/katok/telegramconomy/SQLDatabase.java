package com.katok.telegramconomy;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLDatabase {
    public Connection connection;

    File database_file;

    private static final String main_table = "ids";
    private static final String telegram_id = "id";
    private static final String uuid = "uuid";

    public SQLDatabase(File data_folder) {
        database_file = new File(data_folder + File.separator + "database.db");

        if(!database_file.exists()) {
            try {
                database_file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + database_file);

            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + main_table + " (" + telegram_id + " INTEGER, " + uuid +" TEXT);");
            statement.executeUpdate();
            statement.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PreparedStatement select(String select, String where, String must_be) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT " + select + " FROM " + main_table + " WHERE " + where + " = ?;");
            statement.setString(1, must_be);
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ResultSet select(String select) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT " + select + " FROM " + main_table + ";");
            ResultSet resultSet = statement.executeQuery();
            statement.close();
            return resultSet;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String update, String set, String where, String must_be) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE " + main_table + " SET " + update + " = ? WHERE " + where + " = ?;");
            statement.setString(1, set);
            statement.setString(2, must_be);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(String UUID, String telegram_id) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + main_table + " (" + SQLDatabase.uuid + ", " + SQLDatabase.telegram_id + ") VALUES (?, ?);");
            statement.setString(1, UUID);
            statement.setString(2, telegram_id);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void drop_by_uuid(String UUID) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + main_table + " WHERE " + SQLDatabase.uuid + " = ?;");
            statement.setString(1, UUID);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void drop_by_telegram_id(String telegram_id) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM " + main_table + " WHERE " + SQLDatabase.telegram_id + " = ?;");
            statement.setString(1, telegram_id);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //static
    public static String getMain_table() {
        return main_table;
    }

    public static String getTelegram_id() {
        return telegram_id;
    }

    public static String getUuid() {
        return uuid;
    }
}
