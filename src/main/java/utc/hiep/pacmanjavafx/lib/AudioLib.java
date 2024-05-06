package utc.hiep.pacmanjavafx.lib;

import javafx.scene.media.Media;

import java.util.Objects;

public class AudioLib {
    public static Media siren1 = new Media(Objects.requireNonNull(AudioLib.class.getResourceAsStream("siren_1.wav")).toString());
}
