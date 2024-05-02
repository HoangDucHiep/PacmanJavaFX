package utc.hiep.pacmanjavafx.event;

import javafx.event.EventHandler;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import utc.hiep.pacmanjavafx.scene.GeneralScene;

import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

public class KeyListener {
    private GeneralScene scene;
    private List<KeyType> pressedKey = new Stack<>();
    private Consumer<KeyEvent> keyAction;


    public KeyListener(GeneralScene scene) {
        this.scene = scene;
    }

    public void setKeyAction(Consumer<KeyEvent> keyAction) {
        this.keyAction = keyAction;
    }

    public void keyListening() {
        if(keyAction != null) {
            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                public void handle(KeyEvent e) {
                    keyAction.accept(e);
                }
            });
        } else
            throw new NullPointerException("KeyAction is null, please set it before listening");

    }

    public List<KeyType> getPressedKey() {
        return pressedKey;
    }


    public void clearKey() {
        pressedKey.clear();
    }
}
