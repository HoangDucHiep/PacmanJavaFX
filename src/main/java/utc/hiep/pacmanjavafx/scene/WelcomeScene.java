package utc.hiep.pacmanjavafx.scene;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import utc.hiep.pacmanjavafx.lib.FontLib;
import utc.hiep.pacmanjavafx.lib.Global;
import utc.hiep.pacmanjavafx.lib.ImageLib;

public class WelcomeScene extends GeneralScene {

    private Button startButton;
    private Button scoreButton;
    private Button exitButton;
    private static Font textFont = FontLib.EMULOGIC(20);

    private VBox container;

    public WelcomeScene() {
        super();
        setBackGround();
        container = new VBox();
        container.setMaxWidth(Global.WINDOW_WIDTH);
        container.setAlignment(javafx.geometry.Pos.CENTER);

        getRootPane().getChildren().add(container);

        addSpacer();

        drawLogo();

        startButton = new Button("Start");
        scoreButton = new Button("Scoreboard");
        exitButton = new Button("Exit");

        startButton.setFont(textFont);
        startButton.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        startButton.setTextFill(Color.WHITE);
        startButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);


        scoreButton.setFont(textFont);
        scoreButton.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        scoreButton.setTextFill(Color.WHITE);
        scoreButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        exitButton.setFont(textFont);
        exitButton.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        exitButton.setTextFill(Color.WHITE);
        exitButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        addSpacer();

        drawButton(startButton);
        drawButton(scoreButton);
        drawButton(exitButton);

        addSpacer();

    }

    @Override
    public void render() {
    }



    /**
     * Set background for main pane
     */
    private void setBackGround() {
        BackgroundImage background = new BackgroundImage(ImageLib.MENU_SCENE_BG, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        getRootPane().setBackground(new Background(background));
    }

    private void drawLogo() {
        Image logo = ImageLib.GAME_LOGO;
        ImageView logoView = new ImageView(logo);
        container.getChildren().add(logoView);
    }


    private void drawButton(Button btn) {
        VBox.setMargin(btn, new Insets(5, 0, 5, 0));
        container.getChildren().add(btn);
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getScoreButton() {
        return scoreButton;
    }

    public Button getExitButton() {
        return exitButton;
    }



    private Pane getSpacer() {
        Pane spacer = new Pane();
        spacer.setMinSize(0, 0);
        spacer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private void addSpacer() {
        container.getChildren().add(getSpacer());
    }


}



//old one
//package utc.hiep.pacmanjavafx.scene;
//
//import javafx.geometry.Insets;
//import javafx.scene.control.Button;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.*;
//        import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import utc.hiep.pacmanjavafx.lib.FontLib;
//import utc.hiep.pacmanjavafx.lib.Global;
//import utc.hiep.pacmanjavafx.lib.ImageLib;
//
//public class WelcomeScene extends GeneralScene {
//
//    private Button startButton;
//    private Button scoreButton;
//    private Button exitButton;
//    private static Font textFont = FontLib.EMULOGIC(20);
//
//    public WelcomeScene() {
//        super();
//        setRootPane(new AnchorPane());
//        setBackGround();
//        drawLogo();
//        startButton = new Button("Start");
//        scoreButton = new Button("Scoreboard");
//        exitButton = new Button("Exit");
//
//        startButton.setFont(textFont);
//        startButton.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
//        startButton.setTextFill(Color.WHITE);
//        startButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
//
//        scoreButton.setFont(textFont);
//        scoreButton.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
//        scoreButton.setTextFill(Color.WHITE);
//        scoreButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
//
//        exitButton.setFont(textFont);
//        exitButton.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
//        exitButton.setTextFill(Color.WHITE);
//        exitButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
//
//        drawButton(startButton, 570);
//        drawButton(scoreButton, 620);
//        drawButton(exitButton, 670);
//
//
//
//    }
//
//    @Override
//    public void render() {
//    }
//
//
//
//    /**
//     * Set background for main pane
//     */
//    private void setBackGround() {
//        BackgroundImage background = new BackgroundImage(ImageLib.MENU_SCENE_BG, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
//        getRootPane().setBackground(new Background(background));
//    }
//
//    private void drawLogo() {
//        Image logo = ImageLib.GAME_LOGO;
//        ImageView logoView = new ImageView(logo);
//        AnchorPane.setTopAnchor(logoView, 50.0);
//        AnchorPane.setLeftAnchor(logoView,  (double) (Global.WINDOW_WIDTH / 2 - logo.getWidth()/2));
//        getRootPane().getChildren().add(logoView);
//    }
//
//
//    private void drawButton(Button btn, double y) {
//        btn.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
//            double x = (Global.WINDOW_WIDTH - newValue.getWidth()) / 2;
//            AnchorPane.setLeftAnchor(btn, x);
//        });
//        AnchorPane.setTopAnchor(btn, y);
//        getRootPane().getChildren().add(btn);
//    }
//
//    public Button getStartButton() {
//        return startButton;
//    }
//
//    public Button getScoreButton() {
//        return scoreButton;
//    }
//
//    public Button getExitButton() {
//        return exitButton;
//    }
//
//}
