package net.flarepowered.core.data.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariDatabase {

    private HikariConfig config = new HikariConfig();
    private HikariDataSource ds;

    /**
     * JDBC Hikari Pool
     * @driver You can select the driver of the JDBC url (mysql, mariadb)
     */
    public HikariDatabase(String driver, String host, int port, String database, String password, String username, String flags) {
        config.setJdbcUrl("jdbc:" + driver + "://" + host + ":" + port + "/" + database + "?" + flags);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.setConnectionTestQuery("SELECT 1");
        ds = new HikariDataSource( config );
    }


    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public HikariConfig getConfig() {
        return config;
    }
}
