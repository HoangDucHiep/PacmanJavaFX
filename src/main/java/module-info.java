module utc.hiep.pacmanjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires mysql.connector.java;


    opens utc.hiep.pacmanjavafx to javafx.fxml;
    exports utc.hiep.pacmanjavafx;
    exports utc.hiep.pacmanjavafx.model;
    opens utc.hiep.pacmanjavafx.model to javafx.fxml;
}