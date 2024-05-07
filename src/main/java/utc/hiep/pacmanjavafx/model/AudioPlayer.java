package utc.hiep.pacmanjavafx.model;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioPlayer {
    private List<AudioClip> audios;
    private Map<Integer, AudioClip> playingAudios;

    public AudioPlayer(List<AudioClip> audios) {
        this.audios = audios;
        playingAudios = new HashMap<>();
    }

    public AudioPlayer(AudioClip[] audios) {
        this(List.of(audios));
    }

    public void playSound(byte audioIndex) {
        playSound(audioIndex, false);
    }

    public void playSound(byte audioIndex, boolean loop) {
        if(playingAudios.containsKey((int) audioIndex))
            playingAudios.get((int) audioIndex).stop();
        AudioClip audio = audios.get(audioIndex);
        audio.setCycleCount(loop ? AudioClip.INDEFINITE : 1);
        audio.play();
        playingAudios.put((int) audioIndex, audio);
    }

    public void stopSound(byte audioIndex) {
        if(playingAudios.containsKey((int) audioIndex))
            playingAudios.get((int) audioIndex).stop();
    }


}
