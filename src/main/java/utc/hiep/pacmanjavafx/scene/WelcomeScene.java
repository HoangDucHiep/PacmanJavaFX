package utc.hiep.pacmanjavafx.scene;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import utc.hiep.pacmanjavafx.controller.GameController;
import utc.hiep.pacmanjavafx.lib.Global;

public class WelcomeScene extends GeneralScene {

    private final Button startButton;
    private final Button scoreButton;
    private final Button exitButton;

    private final VBox container;

    public WelcomeScene(GameController game) {
        super();
        setBackGround(GameController.rm().getImage(GameController.MENUBG));
        container = new VBox();
        container.setMaxWidth(Global.WINDOW_WIDTH);
        container.setAlignment(javafx.geometry.Pos.CENTER);
        getRootPane().getChildren().add(container);

        addSpacer();

        drawLogo();

        ImageView pacAndGhostsGif = new ImageView(GameController.rm().getImage("pac_and_ghost"));
        container.getChildren().add(pacAndGhostsGif);
        VBox.setMargin(pacAndGhostsGif, new Insets(Global.TILE_SIZE * 3, 0, 0, 0));


        startButton = newButton("Start", Global.TILE_SIZE * 1.3, Color.WHITE);
        scoreButton = newButton("Scoreboard", Global.TILE_SIZE * 1.3, Color.WHITE);
        exitButton = newButton("Exit", Global.TILE_SIZE * 1.3, Color.WHITE);

        setButtonAction(startButton,
                e -> game.initNewGame(),
                e -> {
                    startButton.setTextFill(Color.color(0.98, 0.81, 0.02));
                    startButton.setText("> Start <");
                },
                e -> {
                    startButton.setTextFill(Color.WHITE);
                    startButton.setText("Start");
                }
        );

        setButtonAction(scoreButton,
                e -> game.changeScene(GameController.SCORE_SCENE),
                e -> {
                    scoreButton.setTextFill(Color.color(0.98, 0.81, 0.02));
                    scoreButton.setText("> Scoreboard <");
                },
                e -> {
                    scoreButton.setTextFill(Color.WHITE);
                    scoreButton.setText("Scoreboard");
                }
        );

        setButtonAction(exitButton,
                e -> game.closeProgram(),
                e -> {
                    exitButton.setTextFill(Color.color(0.98, 0.81, 0.02));
                    exitButton.setText("> Exit <");
                },
                e -> {
                    exitButton.setTextFill(Color.WHITE);
                    exitButton.setText("Exit");
                }
        );


        addSpacer();

        drawButton(startButton);
        drawButton(scoreButton);
        drawButton(exitButton);

        addSpacer();

    }


    private void drawLogo() {
        Image logo = GameController.rm().getImage(GameController.GAME_LOGO);
        ImageView logoView = new ImageView(logo);
        container.getChildren().add(logoView);
    }

    private void drawButton(Button btn) {
        container.getChildren().add(btn);
    }

    private void addSpacer() {
        container.getChildren().add(getVSpacer());
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
