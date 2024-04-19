package utc.hiep.pacmanjavafx.event;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import utc.hiep.pacmanjavafx.scene.GeneralScene;

import java.util.List;
import java.util.Stack;

public class KeyListener {
    private GeneralScene scene;
    private List<KeyType> pressedKey = new Stack<>();


    public KeyListener(GeneralScene scene) {
        this.scene = scene;
    }

    public void keyListening() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void	handle(KeyEvent e){

                switch(e.getCode()) {
                    case UP, W -> {pressedKey.add(KeyType.TURN_UP);
                        System.out.println("Pressed Up");}
                    case DOWN, S -> {pressedKey.add(KeyType.TURN_DOWN);
                        System.out.println("Pressed Down");}
                    case LEFT, A -> {pressedKey.add(KeyType.TURN_LEFT);
                        System.out.println("Pressed Left");}
                    case RIGHT, D -> {pressedKey.add(KeyType.TURN_RIGHT);
                        System.out.println("Pressed Right");}
                    case G -> {pressedKey.add(KeyType.GRID_SWITCH);}
                    default -> System.out.println("Hello");
                }
            }
        });
    }

    public List<KeyType> getPressedKey() {
        return pressedKey;
    }


    public void clearKey() {
        pressedKey.clear();
    }
}
