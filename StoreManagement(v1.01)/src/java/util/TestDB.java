/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author PCQN
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class TestDB {

    public static void main(String[] args) {

        try {

            // Tạo kết nối từ DBContext
            DBContext db = new DBContext();

            Connection conn = db.connection;

            // Kiểm tra kết nối
            if (conn != null && !conn.isClosed()) {

                System.out.println("=================================");
                System.out.println("DATABASE CONNECT SUCCESS");
                System.out.println("=================================");

                // In thông tin DB
                DatabaseMetaData meta = conn.getMetaData();

                System.out.println("Database : "
                        + meta.getDatabaseProductName());

                System.out.println("Version  : "
                        + meta.getDatabaseProductVersion());

                System.out.println("Driver   : "
                        + meta.getDriverName());

                System.out.println("URL      : "
                        + meta.getURL());

                System.out.println("User     : "
                        + meta.getUserName());

                System.out.println("=================================");

            } 
        }catch (Exception e) {

            System.out.println("ERROR CONNECT DATABASE");

            e.printStackTrace();
        }
    }
}