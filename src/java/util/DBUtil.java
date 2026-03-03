package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // THAY ĐỔI: sửa theo cấu hình SQL Server của bạn
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=SchoolAssetManagement;encrypt=false;trustServerCertificate=true";
    private static final String DB_USER = "sa";         // hoặc user SQL của bạn
    private static final String DB_PASS = "123";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
