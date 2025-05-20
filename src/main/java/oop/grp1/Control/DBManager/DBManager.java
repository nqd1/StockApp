package oop.grp1.Control.DBManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DBManager {
    protected String dbUrl;
    
    public void fetchAndStore(String ticker){};

    public DBManager(String dbUrl) {
        this.dbUrl = dbUrl;
    }
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }
    public abstract void saveToDB(String json);

}
