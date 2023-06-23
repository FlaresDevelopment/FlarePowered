package net.flarepowered.core.data.MySQL;

import net.flarepowered.other.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLHandler {

    /**
     * Create a table in a SQL database
     * @param connection The connection (null/not null)
     * @param table the table name
     * @param columns the columns for the connection. Like: (id INT PRIMARY KEY, name VARCHAR(255))
     */
    public static void createTableIfNotExists(Connection connection, String table, String columns) {
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " (" + columns + ");";
        if (connection != null) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                Logger.error("Update failed. We have the error message: " + e.getMessage());
            }
        } else {
            Logger.error("Connection is null please check the database or connection (null).");
        }
    }

    /**
     * This will return if the data exists in a database
     * using the SQL SELECT operation.
     * @param connection The connection (null/not null)
     * @param table the table name
     * @param column the name of the key (where in the statement)
     * @param data The value that will be checked for (WHERE column = data)
     */
    public static boolean exists(Connection connection, String table, String column, String data) {
        String sql = "SELECT * FROM " + table + " WHERE " + column + "='" + data + "';";
        if (connection != null) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                Logger.error("Update failed. We have the error message: " + e.getMessage());
            }
        } else {
            Logger.error("Connection is null please check the database or connection (null).");
        }
        return false;
    }

    /**
     * This will return if the data exists in a database
     * using the SQL SELECT operation.
     * @param connection The connection (null/not null)
     * @param table the table name
     * @param column the name of the key (where in the statement)
     * @param data The value that will be checked for (WHERE column = data)
     */
    public static Object get(Connection connection, String table, String column, String data) throws SQLException {
        String sql = "SELECT * FROM " + table + " WHERE " + column + "='" + data + "';";
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connection != null) {
            try {
                ps = connection.prepareStatement(sql);
                rs = ps.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                Logger.error("Update failed. We have the error message: " + e.getMessage());
            } finally {
                ps.close();
                connection.close();
                rs.close();
            }
        } else {
            Logger.error("Connection is null please check the database or connection (null).");
        }
        return false;
    }

    /**
     * With this method you can get an object from a key.
     * @param connection The connection (null/not null)
     * @param table the table name
     * @param whatValue the name of the key (where in the statement)
     * @param arguments The value that will be checked for (WHERE column = data)
     */
    public static Object get(Connection connection, String table, String whatValue, String[] arguments) {
        StringBuilder data = new StringBuilder();
        for (String argument : arguments)
            data.append(argument).append(" AND ");
        if(connection == null) {
            Logger.error("Connection is null please check the database or connection (null).");
            return null;
        }
        if (data.length() <= 5) {
            return false;
        } else {
            data = new StringBuilder(data.substring(0, data.length() - 5));
            String sql = "SELECT * FROM " + table + " WHERE " + data + ";";
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = connection.prepareStatement(sql);
                rs = ps.executeQuery();
                Object obj = 0;
                if (rs.next())
                    obj = rs.getObject(whatValue);
                ps.close();
                connection.close();
                rs.close();
                return obj;
            }
            catch (SQLException e) {
                Logger.error("Update failed. We have the error message: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * With this method you can get an object from a key.
     * @param connection The connection (null/not null)
     * @param table the table name
     * @param whatValue the name of the key (where in the statement)
     * @param column the key (column)
     * @param logic the operation logic (= != etc)
     * @param data the data compared
     */
    public static Object get(Connection connection, String table, String whatValue, String column, String logic, String data) {
        if(connection == null) {
            Logger.error("Connection is null please check the database or connection (null).");
            return null;
        }

        String sql = "SELECT * FROM " + table + " WHERE " + column + logic + data + ";";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            Object obj = 0;
            if (rs.next())
                obj = rs.getObject(whatValue);
            ps.close();
            connection.close();
            rs.close();
            return obj;
        }
        catch (SQLException e) {
            Logger.error("Update failed. We have the error message: " + e.getMessage());
        }

        return null;
    }

    /**
     * With this method you can get an object from a key.
     * @param connection The connection (null/not null)
     * @param table the table name
     * @param whatValue the name of the key (where in the statement)
     * @param arguments The value that will be checked for (WHERE column = data)
     */
    public static void setMultiple(Connection connection, String table, String whatValue, String[] arguments) {
        StringBuilder args = new StringBuilder();

        for (String argument : arguments) {
            args.append(argument).append(" AND ");
        }

        if (args.length() <= 5) {
        } else {
            args = new StringBuilder(args.substring(0, args.length() - 5));
            String sql = "UPDATE " + table + " SET " + whatValue + " WHERE " + args + ";";
            PreparedStatement ps = null;
            try {
                ps = connection.prepareStatement(sql);
                ps.executeUpdate();
                ps.close();
                connection.close();
            } catch (SQLException e) {
                Logger.error("Update failed. We have the error message: " + e.getMessage());
            }
        }
    }

    public static void set(Connection connection, String table, String whatValue, Object object, String[] arguments) {
        StringBuilder args = new StringBuilder();

        for (String argument : arguments) {
            args.append(argument).append(" AND ");
        }

        if (args.length() <= 5) {
        } else {
            args = new StringBuilder(args.substring(0, args.length() - 5));
            if (object != null) object = "'" + object + "'";
            String sql = "UPDATE " + table + " SET " + whatValue + "=" + object + " WHERE " + args + ";";
            PreparedStatement ps = null;
            try {
                ps = connection.prepareStatement(sql);
                ps.executeUpdate();
                ps.close();
                connection.close();
            } catch (SQLException e) {
                Logger.error("Update failed. We have the error message: " + e.getMessage());
            }
        }
    }

    /**
     * This function insert into a table for the values in columns
     * @param connection The connection (null/not null)
     * @param table the table name
     * @param columns The columns (keys)
     * @param values the data in order
     */
    public static void insertData(Connection connection, String table, String columns, String values) {
        if(connection == null) {
            Logger.error("Connection is null please check the database or connection (null).");
            return;
        }
        String sql = "INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ");";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            Logger.error("Update failed. We have the error message: " + e.getMessage());
        }
    }
}
