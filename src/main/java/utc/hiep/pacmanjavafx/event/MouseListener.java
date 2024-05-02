package utc.hiep.pacmanjavafx.event;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

public class MouseListener {
    Button btn;
    public MouseListener(Button btn) {
        this.btn = btn;
    }

    public void setMouseAction(Consumer<MouseEvent> mouseAction, Consumer<MouseEvent> mouseHover, Consumer<MouseEvent> mouseExit) {
        if(mouseAction != null)
            btn.setOnMouseClicked(mouseAction::accept);
        if(mouseHover != null)
            btn.setOnMouseEntered(mouseHover::accept);
        if(mouseExit != null)
            btn.setOnMouseExited(mouseExit::accept);
    }
}
