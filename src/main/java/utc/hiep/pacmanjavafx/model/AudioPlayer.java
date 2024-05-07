package utc.hiep.pacmanjavafx.model;

import javafx.scene.media.AudioClip;

public class AudioPlayer {
    long playedTime;
    private final AudioClip audioClip;

    public AudioPlayer(AudioClip audioClip) {
        this.audioClip = audioClip;
    }

    public AudioClip getAudioClip() {
        return audioClip;
    }

    public void play() {
        playedTime = System.currentTimeMillis();
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
