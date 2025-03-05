package com.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    final static String JDBC_USER = "root"; 
    final static String JDBC_URL = "jdbc:mysql://localhost:3306/demo"; 
    final static String JDBC_PASSWORD = "password"; 
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(JDBC_URL,JDBC_USER,JDBC_PASSWORD);
	}
}
