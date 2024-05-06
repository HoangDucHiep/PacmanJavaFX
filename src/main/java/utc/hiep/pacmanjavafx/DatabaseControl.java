package utc.hiep.pacmanjavafx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseControl {
    public record HighScore(Integer gamePlayID, String playerName, long score, int level) { }

    static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/pacmanscoredb?serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "";

    static final String SELECT_QUERY = "SELECT GamePlayID, PlayerName, Score, Level FROM scoreboard";
    static final String INSERT_QUERY = "INSERT INTO scoreboard (PlayerName, Score, Level) VALUES (?, ?, ?)";

    private List<HighScore> scoreboard = new ArrayList<>();

    Connection conn = null;
    Statement stmt = null;

    public DatabaseControl() throws SQLException, ClassNotFoundException {
        try {
            System.out.println("STEP 1: Register JDBC driver");
            Class.forName(DRIVER_CLASS);
            System.out.println("STEP 2: Open a connection");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("STEP 3: Execute a query");
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT_QUERY);
            System.out.println("STEP 4: Extract data from result set");
            while (rs.next()) {
                int id = rs.getInt("GamePlayID");
                String name = rs.getString("PlayerName");
                int score = rs.getInt("Score");
                int level = rs.getInt("Level");

                scoreboard.add(new HighScore(id, name, score, level));
            }
            rs.close();
        }catch (SQLException e) {
            throw e;
        }
    }


    public List<HighScore> scoreboard() {
        scoreboard.sort((a, b) -> Math.toIntExact(b.score() - a.score()));
        return scoreboard;
    }

    public void addScore(HighScore score) {
        // Add to database
        try {
            PreparedStatement pstmt = conn.prepareStatement(INSERT_QUERY);
            pstmt.setString(1, score.playerName());
            pstmt.setLong(2, score.score());
            pstmt.setInt(3, score.level());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        scoreboard.add(score);
    }

    public void closeConnection() {
            System.out.println("STEP 5: Close connection");
            if (stmt != null && conn != null){
                try {
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
    }
}
