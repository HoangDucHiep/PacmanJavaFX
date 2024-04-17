module utc.hiep.pacmanjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens utc.hiep.pacmanjavafx to javafx.fxml;
    exports utc.hiep.pacmanjavafx;
}