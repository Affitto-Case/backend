package com.giuseppe_tesse.turista.util;

// Pattern Singleton
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static String url;
    private static String user;
    private static String pwd;
    private static boolean initialized = false;

    private DatabaseConnection() {
    }

    public static void init(String propsPath) {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream(propsPath)) {
            
            if (input == null) {
                throw new RuntimeException("Error: props file not found: " + propsPath);
            }
            
            props.load(input);
            
            // AGGIUNGI QUESTE RIGHE - Assegna i valori alle variabili statiche
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            pwd = props.getProperty("db.password");
            
            // Imposta il flag di inizializzazione
            initialized = true;
            
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {

        if (!initialized) {
            throw new RuntimeException("Execute init() first!");
        }

        // Il driverManager me lo prendo dal package di postgresql
        return DriverManager.getConnection(url, user, pwd);

    }
}