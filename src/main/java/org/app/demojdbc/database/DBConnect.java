package org.app.demojdbc.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/db_school?useSSl=false";
    private final String username = "root";
    private final String password = "123456@Abc";


    public DBConnect() {}

    public Connection getConnect() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("connect success");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return conn;
    }
}
