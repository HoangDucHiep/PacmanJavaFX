package utc.hiep.pacmanjavafx;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioPlayer {
    private List<Media> audios;
    private Map<Integer, MediaPlayer> playingAudios;
    private MediaPlayer mediaPlayer;

    public AudioPlayer(List<Media> audios) {
        this.audios = audios;
        playingAudios = new HashMap<>();
    }

    public AudioPlayer(Media[] audios) {
        this(List.of(audios));
    }

    public void playSound(byte audioIndex) {
        mediaPlayer =  new MediaPlayer(audios.get(audioIndex));
    }

    public void playSound(byte audioIndex, boolean loop) {
        mediaPlayer = new MediaPlayer(audios.get(audioIndex));
        playingAudios.put((int) audioIndex, mediaPlayer);
        if(loop) {
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
        mediaPlayer.play();
    }

    public void stopSound(byte audioIndex) {
        if(playingAudios.containsKey((int) audioIndex))
            playingAudios.get((int) audioIndex).stop();
        else
            System.out.println("This audio is not playing");
    }
}
