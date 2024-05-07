package utc.hiep.pacmanjavafx.lib;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class AudioLib {

    long playTime;


    private AudioClip audioClip;

    public AudioLib(String soundName) {
        audioClip = new AudioClip(Objects.requireNonNull(AudioLib.class.getResource("asserts/sound/" + soundName)).toExternalForm());
    }

    public AudioClip getAudioClip() {
        return audioClip;
    }

    public void play() {
        playTime = System.nanoTime();
        audioClip.play();
    }

    public boolean isPLaying() {
        return audioClip.isPlaying();
    }

    public void stop() {
        audioClip.stop();
        playTime = 0;
    }

    public double playedSec() {
        return (System.nanoTime() - playTime) / 1_000_000_000.0;
    }

}
