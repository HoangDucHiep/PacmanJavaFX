package utc.hiep.pacmanjavafx.scene;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import utc.hiep.pacmanjavafx.lib.FontLib;
import utc.hiep.pacmanjavafx.lib.Global;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;


public abstract class GeneralScene extends Scene {

    private Pane root;

    private final HashSet<KeyCode> pressedKeys = new HashSet<>();


    public GeneralScene() {
        super(new Group(), Global.WINDOW_WIDTH, Global.WINDOW_HEIGHT);
        setRootPane(new StackPane());
    }

    public Pane getRootPane() {
        return root;
    }

    public void setRootPane(Pane root) {
        this.root = root;
        this.setRoot(root);

        this.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
    }

    public HashSet<KeyCode> getPressedKeys() {
        HashSet<KeyCode> keys = new HashSet<>(this.pressedKeys);
        this.pressedKeys.clear();
        return keys;
    }

    public static void setButtonAction(Button button, Consumer<MouseEvent> onclicked, Consumer<MouseEvent> onEntered, Consumer<MouseEvent> onExited) {
        button.setOnMouseClicked(onclicked::accept);
        button.setOnMouseEntered(onEntered::accept);
        button.setOnMouseExited(onExited::accept);
    }

    public void setBackGround(Image bgImg) {
        BackgroundImage background = new BackgroundImage(bgImg, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        getRootPane().setBackground(new Background(background));
    }

    public void setBackGround(Image bgImg, BackgroundRepeat repeat) {
        BackgroundImage background = new BackgroundImage(bgImg, repeat, repeat, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        getRootPane().setBackground(new Background(background));
    }

    public void setBackGound(Color bgColor) {
        getRootPane().setBackground(new Background(new BackgroundFill(bgColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }


    public static Button newButton(String text, double fontSize, Color fill) {
        return newButton(text, fontSize, fill, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
    }

    public static Button newButton(String text, double fontSize, Color fill, double width, double height) {
        Button button = new Button(text);
        button.setFont(FontLib.EMULOGIC(fontSize));
        button.setTextFill(fill);
        button.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        button.setTextFill(Color.WHITE);
        button.setPrefSize(width, height);
        return button;
    }


    public static Pane getVSpacer() {
        Pane spacer = new Pane();
        spacer.setMinSize(0, 0);
        spacer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }


}