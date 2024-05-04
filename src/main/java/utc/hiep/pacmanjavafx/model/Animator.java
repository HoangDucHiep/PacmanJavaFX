package utc.hiep.pacmanjavafx.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utc.hiep.pacmanjavafx.lib.Direction;

import static utc.hiep.pacmanjavafx.lib.Global.HALF_TILE_SIZE;
import static utc.hiep.pacmanjavafx.lib.Global.TILE_SIZE;


/**
 * Class for animation, update and return the current frame position in each rendering
 * @author HoangHiep
 * @version 1.0
 * @implNote This class is used for both non-direction and direction animation, use {@link Animator#getDirAnimator} or {@link Animator#getNonDirAnimator} to get an animator
 * @see #getNonDirAnimator(Image, int, int, int, AnimatorPos[])
 * @see #update()
 * @see #getDirAnimator(Image, int, int, int, AnimatorPos[], AnimatorPos[], AnimatorPos[], AnimatorPos[])
 * @see #update(Direction)
 * @see #getAnimationPos()
 * @see AnimatorPos
 */

public class Animator {
    //For animation
    private int animationCount;
    private int animationDir;
    private long ticksCount;
    private final int changeRate;

    //for checking dir- or not
    private boolean isDirAnimator;

    private AnimatorPos[][] ANIMATOR_SPRITE;
    private final Image sheet;
    private final int sizeInSheet;
    private final int ratio;

    private Animator(int changeRate, Image sheet, int sizeInSheet, int realSize) {
        this.sheet = sheet;
        this.changeRate = changeRate;
        this.sizeInSheet = sizeInSheet;
        this.ratio = realSize;
        ticksCount = 0;
    }


    /* Non-direction animation */

    /**
     * Create a non-direction animator
     * @param changeRate the rate of changing animation
     * @param animatorPos frame position in sprite sheet
     * @return a non-direction animator
     */
    public static Animator getNonDirAnimator(Image sheet, int changeRate, int sizeInSheet, int ratio, AnimatorPos[] animatorPos) {
        Animator animator = new Animator(changeRate, sheet, sizeInSheet, ratio);
        animator.animationDir = 0;
        setAnimatorPos(animator, animatorPos);
        animator.isDirAnimator = false;
        return animator;
    }

    /**
     * Update the animator for non-direction animation
     * @throws IllegalStateException if the animator is a direction animator
     */
    public void update() {
        if(isDirAnimator) {
            throw new IllegalStateException("Can't use this for direction animator, use update(Direction) instead");
        }
        tickUpdate();
    }

    private static void setAnimatorPos(Animator an, AnimatorPos[] animatorPos) {
        an.ANIMATOR_SPRITE = new AnimatorPos[][]{animatorPos};
    }


    /* Direction animator */
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    /**
     * Create a direction animator
     * @param changeRate the rate of changing animation
     * @param left frame position in sprite sheet for left direction
     * @param right frame position in sprite sheet for right direction
     * @param up frame position in sprite sheet for up direction
     * @param down frame position in sprite sheet for down direction
     * @return a direction animator
     */
    public static Animator getDirAnimator(Image sheet, int changeRate, int sizeInSheet, int ratio,AnimatorPos[] left, AnimatorPos[] right, AnimatorPos[] up, AnimatorPos[] down) {
        Animator animator = new Animator(changeRate, sheet, sizeInSheet, ratio);
        setAnimatorPos(animator, left, right, up, down);
        animator.isDirAnimator = true;
        return animator;
    }

    /**
     * Update the animator for direction animation
     * @param movingDir the direction of moving
     * @throws IllegalStateException if the animator is not a direction animator
     */
    public void update(Direction movingDir) {
        if(!isDirAnimator) {
            throw new IllegalStateException("Can't use this for non-direction animator, use update() instead");
        }
        tickUpdate();
        switch (movingDir) {
            case LEFT -> animationDir = LEFT;
            case RIGHT -> animationDir = RIGHT;
            case UP -> animationDir = UP;
            case DOWN -> animationDir = DOWN;
        }
    }

    private static void setAnimatorPos(Animator an, AnimatorPos[] left, AnimatorPos[] right, AnimatorPos[] up, AnimatorPos[] down) {
        an.ANIMATOR_SPRITE = new AnimatorPos[][]{left, right, up, down};
    }


    /* Use for both */

    /**
     * Get the current frame position in sprite sheet
     * @return the current frame position in sprite sheet
     */
    public AnimatorPos getAnimationPos() {
        return ANIMATOR_SPRITE[animationDir][animationCount];
    }

    public void render(GraphicsContext gc, double x, double y) {
        gc.drawImage(sheet, getAnimationPos().posX, getAnimationPos().posY(), sizeInSheet, sizeInSheet, x - HALF_TILE_SIZE, y - HALF_TILE_SIZE, TILE_SIZE * ratio, TILE_SIZE * ratio);
    }

    private void tickUpdate() {
        ticksCount++;
        while(ticksCount >= changeRate) {
            animationCount = (animationCount + 1) % ANIMATOR_SPRITE[0].length;
            ticksCount -= changeRate;
        }
    }


    /**
     * Position of a frame in sprite sheet, have x and y position
    * */
    public static class AnimatorPos {
        private int posX;
        private int posY;


        /**
         * Create a frame position in sprite sheet
         * @param posX
         * @param posY
         */
        public AnimatorPos(int posX, int posY) {
            this.posX = posX;
            this.posY = posY;
        }

        /**
         * Get the x position
         * @return the x
         */
        public int posX() {
            return posX;
        }


        /**
         * Get the y position
         * @return the y
         */
        public int posY() {
            return posY;
        }
    }
}
