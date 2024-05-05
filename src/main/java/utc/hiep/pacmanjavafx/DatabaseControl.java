package utc.hiep.pacmanjavafx;

import java.sql.*;
public class DatabaseControl {
    static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/pacmanscoredb?serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "";
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        Statement stmt = null;
        try {
            System.out.println("STEP 1: Register JDBC driver");
            Class.forName(DRIVER_CLASS);
            System.out.println("STEP 2: Open a connection");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("STEP 3: Execute a query");
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT GamePlayID, PlayerName, Score, Level FROM scoreboard");
            System.out.println("STEP 4: Extract data from result set");
            while (rs.next()) {
                int id = rs.getInt("GamePlayID");
                String name = rs.getString("PlayerName");
                int score = rs.getInt("Score");
                int level = rs.getInt("Level");

                System.out.print("ID: " + id);
                System.out.print(", name: " + name);
                System.out.println(", score: " + score);
                System.out.println(", level: " + level);
            }
            rs.close();
        }catch (SQLException e) {
            throw e;
        } finally {
            System.out.println("STEP 5: Close connection");
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        }
        System.out.println("Done!");
    }
}
