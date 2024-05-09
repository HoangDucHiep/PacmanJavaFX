package utc.hiep.pacmanjavafx.model;

import javafx.scene.media.AudioClip;

public class AudioPlayer {
    long playedTime;

    private final AudioClip audioClip;

    public AudioPlayer(AudioClip audioClip) {
        this.audioClip = audioClip;
    }

    public void play() {
        play(false);
    }

    public void play(boolean loop) {
        playedTime = System.currentTimeMillis();
        if(loop) {
            audioClip.setCycleCount(AudioClip.INDEFINITE);
        }
        audioClip.play();
    }

    public void play(int times) {
        playedTime = System.currentTimeMillis();
        audioClip.setCycleCount(times);
        audioClip.play();
    }

    public boolean isPLaying() {
        return audioClip.isPlaying();
    }

    public void stop() {
        audioClip.stop();
        playedTime = 0;
    }

    public double playedSec() {
        return (System.currentTimeMillis() - playedTime) / 1000.0;
    }

}
