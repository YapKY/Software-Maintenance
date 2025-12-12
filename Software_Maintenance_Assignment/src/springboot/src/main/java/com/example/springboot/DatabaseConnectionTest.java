package com.example.springboot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://aws-1-ap-southeast-1.pooler.supabase.com:6543/postgres";
        String username = "postgres.yvtjcubkrmtrfcfiqsif";
        String password = "tarumtmaintenanceasg";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connection successful!");
            connection.close();
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
        }
    }
}