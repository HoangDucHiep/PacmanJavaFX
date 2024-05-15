package utc.hiep.pacmanjavafx.model;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatabaseControl {
    public record HighScore(String playerName, long score, int level) { }

    static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/pacmanscoredb?serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "";

    static final String SELECT_QUERY = "SELECT PlayerName, Score, Level FROM scoreboard";
    static final String INSERT_QUERY = "INSERT INTO scoreboard (PlayerName, Score, Level) VALUES (?, ?, ?)";

    private List<HighScore> scoreboard = new ArrayList<>();

    ExecutorService executor = Executors.newSingleThreadExecutor();

    Connection conn = null;
    Statement stmt = null;

    String userHome = System.getProperty("user.home");
    File directory = new File(userHome, "pacman-javafx");
    File file;

    public DatabaseControl(){

        List<HighScore> temp = new ArrayList<>();

        try {
            file = new File(directory, "backup.txt");
            if (file.exists()) {
                System.out.println("File existed!!!");
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(",");
                        String name = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        int level = Integer.parseInt(parts[2]);

                        HighScore highScore = new HighScore(name, score, level);
                        temp.add(highScore);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Unable to read from backup.txt");
                }
            }
        } catch (Exception e) {
            System.out.println("Error in reading backup.txt : " + e);
        }

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
                String name = rs.getString("PlayerName");
                int score = rs.getInt("Score");
                int level = rs.getInt("Level");
                scoreboard.add(new HighScore(name, score, level));
            }

            rs.close();
        } catch (Exception e) {
            System.out.println("Unable to connect to database. Running in offline mode.");
            scoreboard = new ArrayList<>();
        }


        if (!file.delete()) {
            System.out.println("Unable to delete backup.txt");
        }

        for(HighScore score : temp) {
            addScore(score);
        }
    }


    public List<HighScore> scoreboard() {
        scoreboard.sort((a, b) -> Math.toIntExact(b.score() - a.score()));
        return scoreboard;
    }

    public void addScore(HighScore score)  {
        executor.submit(() -> {
            if (conn != null) {
                try {
                    PreparedStatement pstmt = conn.prepareStatement(INSERT_QUERY);
                    pstmt.setString(1, score.playerName());
                    pstmt.setLong(2, score.score());
                    pstmt.setInt(3, score.level());
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    writeScoreToBackup(score);
                }
            } else {
                writeScoreToBackup(score);
            }
        });

        scoreboard.add(score);
    }

    private void writeScoreToBackup(HighScore score) {
        if (!directory.exists()) {
            boolean result = directory.mkdirs();
            if (!result) {
                System.out.println("Failed to create directory");
                return;
            }
        }
        System.out.println(file.getAbsolutePath());
        System.out.println("Writing to backup.txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(file, true))) {
            System.out.println("Im here");
            out.println(score.playerName() + "," + score.score() + "," + score.level());
        } catch (IOException ioException) {
            System.out.println("Unable to write to backup.txt");
        }
    }


    public void shutdown() {
        System.out.println("STEP 5: Close connection");
        if (stmt != null && conn != null){
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();
        try {
            // Wait for existing tasks to complete
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                // Cancel currently executing tasks
                executor.shutdownNow();
                // Wait for tasks to respond to being cancelled
                if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Executor did not terminate");
            }
        } catch (InterruptedException ie) {
            // Re-cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
