package utc.hiep.pacmanjavafx.lib;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;

public class ResourcesManeger {

    private Map<String, Image> images;
    private Map<String, AudioClip> sounds;


    public ResourcesManeger() {
        images = new HashMap<>();
        sounds = new HashMap<>();
    }

    public void addImage(String name, Image image) {
        images.put(name, image);
    }

    public void addAllImages(Map<String, Image> images) {
        this.images.putAll(images);
    }

    public Image getImage(String name) {
        return images.get(name);
    }

    public void addSound(String name, AudioClip sound) {
        sounds.put(name, sound);
    }

    public void addAllSounds(Map<String, AudioClip> sounds) {
        this.sounds.putAll(sounds);
    }

    public AudioClip getSound(String name) {
        return sounds.get(name);
    }

    public void clear() {
        images.clear();
        sounds.clear();
    }



}
