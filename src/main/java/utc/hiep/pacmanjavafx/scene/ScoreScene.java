package utc.hiep.pacmanjavafx.scene;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import utc.hiep.pacmanjavafx.DatabaseControl;
import utc.hiep.pacmanjavafx.lib.FontLib;
import utc.hiep.pacmanjavafx.lib.Global;
import utc.hiep.pacmanjavafx.lib.ImageLib;
import utc.hiep.pacmanjavafx.model.level.GameModel;

import java.util.List;

public class ScoreScene extends GeneralScene{

    private final GameModel game;

    private VBox logoSide;
    private VBox scoreSide;

    private SubScene newScoreEnterScene;

    AnchorPane rootPane;

    boolean isSubSceneHidden;

    public ScoreScene(GameModel game) {
        super();
        this.game = game;

        rootPane = new AnchorPane();
        scoreSide = new VBox();
        logoSide = new VBox();
        setRootPane(rootPane);
        rootPane.autosize();
        setBackGround();
        createLogoSide();
        createScoreSide();
        createNewScoreSubScene();
    }


    private void createLogoSide() {
        logoSide.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        logoSide.setAlignment(javafx.geometry.Pos.CENTER);
        logoSide.setFillWidth(true);
        HBox.setHgrow(logoSide, Priority.ALWAYS);


        // Bind the preferred width and height of logoSide to the width and height of rootPane
        logoSide.prefWidthProperty().bind(rootPane.widthProperty().divide(2));
        logoSide.prefHeightProperty().bind(rootPane.heightProperty());


        AnchorPane.setLeftAnchor(logoSide, (double)Global.TILE_SIZE * 2);
        AnchorPane.setTopAnchor(logoSide, 0.0);
        AnchorPane.setBottomAnchor(logoSide, 0.0);

        rootPane.getChildren().add(logoSide);


        logoSide.getChildren().add(getSpacer());

        Image logo = ImageLib.SCORE_SCENE_LOGO;
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(logo.getWidth());
        logoView.setFitHeight(logo.getHeight());
        logoSide.getChildren().add(logoView);

        ///
        Font textFont = FontLib.EMULOGIC((int) (Global.TILE_SIZE * 0.8));


        Button backButton = new Button("<< Back to menu and play again!!! >>");
        backButton.setFont(textFont);
        backButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        backButton.setTextFill(Color.WHITE);
        backButton.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        logoSide.getChildren().add(getSpacer());

        logoSide.getChildren().add(backButton);

        logoSide.getChildren().add(getSpacer());
    }


    VBox scoreList;
    private void createScoreSide() {
        scoreSide.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
        scoreSide.setAlignment(javafx.geometry.Pos.CENTER);
        scoreSide.setFillWidth(true);
        HBox.setHgrow(scoreSide, Priority.ALWAYS);

        // Bind the preferred width and height of scoreSide to the width and height of rootPane
        scoreSide.prefWidthProperty().bind(rootPane.widthProperty().divide(2));
        scoreSide.prefHeightProperty().bind(rootPane.heightProperty());

        AnchorPane.setRightAnchor(scoreSide, (double)Global.TILE_SIZE * 2);
        AnchorPane.setTopAnchor(scoreSide, 0.0);
        AnchorPane.setBottomAnchor(scoreSide, 0.0);

        rootPane.getChildren().add(scoreSide);

        Font textFont = FontLib.EMULOGIC((int) (Global.TILE_SIZE * 0.8));
        Text scoreText = new Text("The best 10 highest score played!!!!");
        scoreText.setFont(textFont);
        scoreText.setFill(Color.WHITE);
        scoreSide.getChildren().add(scoreText);
        VBox.setMargin(scoreText, new Insets(0, 0, Global.TILE_SIZE, 0));

        ScrollPane scoreboard = new ScrollPane();
        scoreboard.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        scoreboard.setFitToWidth(true);
        scoreboard.setMinHeight(500);
        scoreboard.setMaxHeight(500);

        scoreList = new VBox();
        scoreboard.setContent(scoreList);


        scoreList.setAlignment(Pos.TOP_CENTER);
        scoreList.setBackground(new Background(new BackgroundFill(Color.PINK, CornerRadii.EMPTY, Insets.EMPTY)));
        loadScoreboard();

        scoreSide.getChildren().add(scoreboard);

    }


    private void loadScoreboard() {
        // Load the scoreboard from the database
        // Display the scoreboard
        scoreList.getChildren().clear();

        List<DatabaseControl.HighScore> scoreboard = game.db().scoreboard();
        for(var highscore : scoreboard) {
            Label score = new Label(highscore.playerName() + " : " + highscore.score());
            score.setFont(FontLib.EMULOGIC((int) (Global.TILE_SIZE * 0.8)));
            score.setTextFill(Color.WHITE);
            scoreList.getChildren().add(score);
        }
    }

    private void createNewScoreSubScene() {
        StackPane newScoreEnterPane = new StackPane();
        newScoreEnterPane.setBackground(new Background(new BackgroundFill(Color.color(0, 0, 0, 0.85), CornerRadii.EMPTY, Insets.EMPTY)));
        newScoreEnterPane.setAlignment(javafx.geometry.Pos.CENTER);
        newScoreEnterScene = new SubScene(newScoreEnterPane, getWidth(), getHeight());
        isSubSceneHidden = true;
        newScoreEnterScene.setVisible(false);
        newScoreEnterScene.setScaleX(0.1);
        newScoreEnterScene.setScaleY(0.1);


        // Bind the width and height properties of newScoreEnterScene to the dimensions of rootPane
        newScoreEnterScene.widthProperty().bind(rootPane.widthProperty());
        newScoreEnterScene.heightProperty().bind(rootPane.heightProperty());

        // Bind the translate properties of newScoreEnterScene to the dimensions of rootPane
        newScoreEnterScene.translateXProperty().bind(rootPane.widthProperty().subtract(newScoreEnterScene.widthProperty()).divide(2));
        getRootPane().getChildren().add(newScoreEnterScene);


        GridPane gp = new GridPane();
        gp.setMinHeight(Region.USE_COMPUTED_SIZE);
        gp.setMinWidth(Region.USE_COMPUTED_SIZE);
        gp.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        StackPane.setAlignment(gp, javafx.geometry.Pos.CENTER);
        newScoreEnterPane.getChildren().add(gp);

        Label newScoreLabel = new Label("GAME OVER");
        newScoreLabel.setTextFill(Color.WHITE);
        newScoreLabel.setFont(FontLib.EMULOGIC((int) (Global.TILE_SIZE * 1.5)));
        GridPane.setHalignment(newScoreLabel, javafx.geometry.HPos.CENTER);
        GridPane.setColumnSpan(newScoreLabel, 2);
        GridPane.setMargin(newScoreLabel, new Insets(0, 0, Global.TILE_SIZE , 0));
        gp.add(newScoreLabel, 1, 0);


        Label name = new Label("NAME: ");
        name.setTextFill(Color.WHITE);
        name.setFont(FontLib.EMULOGIC((int) (Global.TILE_SIZE * 0.8)));
        gp.add(name, 0, 1);

        TextField nameField = new TextField();
        nameField.setFont(FontLib.EMULOGIC((int) (Global.TILE_SIZE * 0.8)));
        nameField.setMinWidth(Global.TILE_SIZE * 15);
        gp.add(nameField, 1, 1);

        Label score = new Label("SCORE: ");
        score.setTextFill(Color.WHITE);
        score.setFont(FontLib.EMULOGIC((int) (Global.TILE_SIZE * 0.8)));
        gp.add(score, 2, 1);
        GridPane.setMargin(score, new Insets(0, 0, 0, Global.TILE_SIZE * 3));

        Text scoreValue = new Text(String.valueOf(game.score()));
        scoreValue.setFill(Color.WHITE);
        scoreValue.setFont(FontLib.EMULOGIC((int) (Global.TILE_SIZE * 0.8)));
        gp.add(scoreValue, 3, 1);

        Button submit = new Button("Submit");
        submit.setFont(FontLib.EMULOGIC((int) (Global.TILE_SIZE * 0.9)));
        submit.setTextFill(Color.WHITE);
        submit.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        submit.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1))));
        gp.add(submit, 1, 2);
        GridPane.setColumnSpan(submit, 2);
        GridPane.setHalignment(submit, javafx.geometry.HPos.CENTER);
        GridPane.setMargin(submit, new Insets(Global.TILE_SIZE, 0, 0, 0));

        submit.setOnMouseClicked(e -> {
            System.out.println("Clicked");
            isSubSceneHidden = false;
            moveSubScene(newScoreEnterScene);
            game.db().addScore(new DatabaseControl.HighScore(null, nameField.getText(), game.score(), game.gameLevel().levelNum()));
            loadScoreboard();
        });

        submit.setOnMouseEntered(e -> {
            submit.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
            submit.setTextFill(Color.BLACK);
        });

        submit.setOnMouseExited(e -> {
            submit.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            submit.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1))));
            submit.setTextFill(Color.WHITE);
        });
    }

    private Pane getSpacer() {
        Pane spacer = new Pane();
        spacer.setMinSize(0, 0);
        spacer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private void setBackGround() {
        BackgroundImage background = new BackgroundImage(ImageLib.BACKGROUND_IMAGE, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        getRootPane().setBackground(new Background(background));
    }

    public void moveSubScene(SubScene sc) {
        ScaleTransition transition = new ScaleTransition();
        transition.setDuration(javafx.util.Duration.seconds(0.4));
        transition.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
        transition.setNode(sc);

        isSubSceneHidden = !isSubSceneHidden;

        if(isSubSceneHidden) {
            transition.setToX(0.1); // thu nhỏ
            transition.setToY(0.1); // thu nhỏ
            transition.setOnFinished(e -> {
                newScoreEnterScene.setVisible(false);
                isSubSceneHidden = false;
            });

        } else {
            newScoreEnterScene.setVisible(true);
            transition.setToX(1); // phóng to
            transition.setToY(1); // phóng to
            isSubSceneHidden = true;
        }

        transition.play();
    }

    public void showNewScoreScene() {
        moveSubScene(newScoreEnterScene);
    }

    @Override
    public void render() {

    }
}
