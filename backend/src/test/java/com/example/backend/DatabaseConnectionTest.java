package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://aws-1-ap-southeast-1.pooler.supabase.com:5432/postgres?sslmode=require",
    "spring.datasource.username=postgres.mqxuqwhfdotevurazduq",
    "spring.datasource.password=x5cv54L0RY3Raxde",
    "spring.datasource.driver-class-name=org.postgresql.Driver"
})
public class DatabaseConnectionTest {

    @Test
    public void testDatabaseConnection() {
        try {
            System.out.println("Testing Supabase database connection...");
            
            // Test direct JDBC connection
            String url = "jdbc:postgresql://aws-1-ap-southeast-1.pooler.supabase.com:5432/postgres?sslmode=require";
            String username = "postgres.mqxuqwhfdotevurazduq";
            String password = "x5cv54L0RY3Raxde";
            
            System.out.println("Connecting to: " + url);
            System.out.println("Username: " + username);
            
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connection successful!");
            
            // Test a simple query
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("SELECT version()");
            if (resultSet.next()) {
                System.out.println("✅ Database version: " + resultSet.getString(1));
            }
            
            connection.close();
            System.out.println("✅ Connection closed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database connection test failed", e);
        }
    }
} 