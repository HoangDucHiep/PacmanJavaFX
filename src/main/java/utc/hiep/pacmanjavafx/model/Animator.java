package utc.hiep.pacmanjavafx.model;

import utc.hiep.pacmanjavafx.lib.Direction;

public class Animator {

    public static class AnimatorPos {
        private int posX;
        private int posY;

        public AnimatorPos(int posX, int posY) {
            this.posX = posX;
            this.posY = posY;
        }

        public int posX() {
            return posX;
        }

        public int posY() {
            return posY;
        }
    }

    //For animation
    private int animationCount;
    private int animationDir;
    private long ticksCount;
    private int changeRate;

    private AnimatorPos[][] ANIMATOR_SPRITE;


    private Animator(int changeRate) {
        this.changeRate = changeRate;
        ticksCount = 0;
    }


    /* Non-direction animation */
    public static Animator getNonDirAnimator(int changeRate, AnimatorPos[] animatorPos) {
        Animator animator = new Animator(changeRate);
        animator.animationDir = 0;
        setAnimatorPos(animator, animatorPos);
        return animator;
    }

    public void update() {
        ticksCount++;
        while(ticksCount >= changeRate) {
            animationCount = (animationCount + 1) % ANIMATOR_SPRITE[0].length;
            ticksCount -= changeRate;
        }
    }

    private static void setAnimatorPos(Animator an, AnimatorPos[] animatorPos) {
        an.ANIMATOR_SPRITE = new AnimatorPos[][]{animatorPos};
    }


    /* Direction animator */

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    public static Animator getDirAnimator(int changeRate, AnimatorPos[] left, AnimatorPos[] right, AnimatorPos[] up, AnimatorPos[] down) {
        Animator animator = new Animator(changeRate);
        setAnimatorPos(animator, left, right, up, down);
        return animator;
    }

    public void update(Direction movingDir) {
        ticksCount++;
        while(ticksCount >= changeRate) {
            animationCount = (animationCount + 1) % ANIMATOR_SPRITE[0].length;
            ticksCount -= changeRate;
        }
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
    public AnimatorPos getAnimationPos() {
        return ANIMATOR_SPRITE[animationDir][animationCount];
    }

}
