package utc.hiep.pacmanjavafx.lib;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.AudioPlayer;
import utc.hiep.pacmanjavafx.model.level.GameModel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResourcesManeger {

    private Map<String, Image> images;
    private Map<String, AudioPlayer> sounds;
    private Map<Byte, Animator> animators;

    public ResourcesManeger() {
        images = new HashMap<>();
        sounds = new HashMap<>();
        animators = new HashMap<>();   
    }

    /**
     * Add all images in original size, if you want to resize the image, use {@link ResourcesManeger#addImage(String, String, int, int)},
     * @param name name of the image
     * @param fileName file name of the image
     */
    public void addImage(String name, String fileName) {
        images.put(name, new Image(Objects.requireNonNull(ResourcesManeger.class.getResourceAsStream("asserts/graphics/" + fileName))));
    }
    
    public void addImage(String name, String fileName, double width, double height) {
        images.put(name, new Image(Objects.requireNonNull(ResourcesManeger.class.getResourceAsStream("asserts/graphics/" + fileName)), width, height, true, true));
    }

    public void addAllImages(Map<String, String> fileName) {
            fileName.forEach(this::addImage);
    }
    
    public Image getImage(String name) {
        return images.get(name);
    }
    
    public void addSound(String name, String fileName) {
        AudioPlayer ap = new AudioPlayer(new AudioClip(Objects.requireNonNull(ResourcesManeger.class.getResource("asserts/sound/" + fileName)).toExternalForm()));
        sounds.put(name, ap);
    }
    
    public void addAllSounds(Map<String, String> FileName) {
        FileName.forEach(this::addSound);
    }
    
    public AudioPlayer getSound(String name) {
        return sounds.get(name);
    }
    
    public void addAnimator(byte id, Animator animator) {
        animators.put(id, animator);
    }
    
    public void addAllAnimators(Map<Byte, Animator> animators) {
        this.animators.putAll(animators);
    }
    
    public Animator getAnimator(int id) {
        return animators.get(id);
    }
    
    
}
