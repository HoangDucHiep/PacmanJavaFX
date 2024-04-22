package utc.hiep.pacmanjavafx.model;

import utc.hiep.pacmanjavafx.lib.Direction;

import static utc.hiep.pacmanjavafx.lib.Global.SPRITE_SHEET_WIDTH;

public class Animator {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private int changeRate;
    
    //private Timer ticker;

    //For animation
    private int animationCount;
    private int animationDir;
    private long ticksCount;

    //to turn it to x and y: x = value / sprite_sheet_width, y = value % sprite_sheet_width
    //location of the current frame in sprite sheet
    private int[][] ANIMATIOR_SPRITE;
    private int spriteSize;



    public Animator(int changeRate) {
        this.changeRate = changeRate;
        ticksCount = 0;
    }

    public void update(Direction movingDir) {
        ticksCount++;
        //long now = ticker.getTick();
        //delta += (int) (now - lastTick);
        while(ticksCount >= changeRate) {
            animationCount = (animationCount + 1) % ANIMATIOR_SPRITE[0].length;
            ticksCount -= changeRate;
        }
        //lastTick = now;

        switch (movingDir) {
            case LEFT -> animationDir = LEFT;
            case RIGHT -> animationDir = RIGHT;
            case UP -> animationDir = UP;
            case DOWN -> animationDir = DOWN;
        }
    }

//    public void setTicker(Timer ticker) {
//        this.ticker = ticker;
//    }

//    public int animationCount() {
//        return animationCount;
//    }

//    public int animationDir() {
//        return animationDir;
//    }

    public int animatorX() {
        return (int) ANIMATIOR_SPRITE[animationDir][animationCount] % SPRITE_SHEET_WIDTH;
    }

    public int animatorY() {
        return (int) ANIMATIOR_SPRITE[animationDir][animationCount] / SPRITE_SHEET_WIDTH * spriteSize;
    }

    public void setANIMATIOR_SPRITE(int[][] ANIMATIOR_SPRITE) {
        this.ANIMATIOR_SPRITE = ANIMATIOR_SPRITE;
    }

    public void setSpirteSize(int spirteSize) {
        this.spriteSize = spirteSize;
    }
    
    public void setChangeRate(int changeRate) {
        this.changeRate = changeRate;
    }




}
