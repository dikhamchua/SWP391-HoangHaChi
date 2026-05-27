package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author FPT University - PRJ30X
 */
public class DBContext {
    protected Connection connection;
    public DBContext() {
        //@Students: You are not allowed to edit this method  
        try {
            Properties properties = new Properties();
            InputStream inputStream = null;

            // Case 1: file nằm cùng package util
            inputStream = DBContext.class.getResourceAsStream("ConnectDB.properties");

            // Case 2: file nằm ở root classpath
            if (inputStream == null) {
                inputStream = DBContext.class.getClassLoader()
                        .getResourceAsStream("ConnectDB.properties");
            }

            // Case 3: file nằm ở util/ConnectDB.properties trong classpath
            if (inputStream == null) {
                inputStream = DBContext.class.getClassLoader()
                        .getResourceAsStream("util/ConnectDB.properties");
            }
            try {
                properties.load(inputStream);
            } catch (IOException ex) {
                Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
            String user = properties.getProperty("userID");
            String pass = properties.getProperty("password");
            String url = properties.getProperty("url");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
