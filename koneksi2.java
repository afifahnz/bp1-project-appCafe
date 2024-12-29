

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class koneksi2 {
    static Connection con;

    public static Connection getConnection() {
        if (con == null) {
            String id = "root";
            String pass = "";
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/db_appcafe";
            
            try {
                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(url, id, pass);
                JOptionPane.showMessageDialog(null, "Koneksi Berhasil");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return con;
    }
}

