package utc.hiep.pacmanjavafx.lib;

import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.level.GameModel;

import static utc.hiep.pacmanjavafx.model.level.GameModel.MAP_FLASHING_RATE;


/**
 * Library for all animators
 * @implNote Position of each sprite calculated based on the sprite sheet in {@link ImageLib#SPRITE_SHEET},
 */
public class AnimatorLib {


    private static final int PACMAN_SIZE = 15;  //size of pacman int sprite_sheet.png
    private static final int GHOST_SIZE = 16;   //size of ghost in sprite_sheet.png
    private static final int ENERGIZER_SIZE = 8;
    /*For pacman*/
    private static final Animator.AnimatorPos[] PACMAN_RIGHT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 0),
            new Animator.AnimatorPos(0, 0),
            new Animator.AnimatorPos(16, 0),
            new Animator.AnimatorPos(32, 0)
    };


    private static final Animator.AnimatorPos[] PACMAN_LEFT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 0),
            new Animator.AnimatorPos(0, 16),
            new Animator.AnimatorPos(16, 16),
            new Animator.AnimatorPos(32, 0)
    };

    private static final Animator.AnimatorPos[] PACMAN_UP = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 0),
            new Animator.AnimatorPos(0, 32),
            new Animator.AnimatorPos(16, 32),
            new Animator.AnimatorPos(32, 0)
    };

    private static final Animator.AnimatorPos[] PACMAN_DOWN = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 0),
            new Animator.AnimatorPos(0, 48),
            new Animator.AnimatorPos(16, 48),
            new Animator.AnimatorPos(32, 0)
    };


    /* For died pacman */
    private static final Animator.AnimatorPos[] DIED_PACMAN_POS = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 0),
            new Animator.AnimatorPos(48, 0),
            new Animator.AnimatorPos(64, 0),
            new Animator.AnimatorPos(80, 0),
            new Animator.AnimatorPos(96, 0),
            new Animator.AnimatorPos(112, 0),
            new Animator.AnimatorPos(128, 0),
            new Animator.AnimatorPos(144, 0),
            new Animator.AnimatorPos(160, 0),
            new Animator.AnimatorPos(176, 0),
            new Animator.AnimatorPos(192, 0),
            new Animator.AnimatorPos(208, 0),
            new Animator.AnimatorPos(176, 32),
            new Animator.AnimatorPos(176, 32),
            new Animator.AnimatorPos(176, 32),

    };


    /* For energizer */
    private static final Animator.AnimatorPos[] ENERGIZER_ANIM_POS = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(0, 0),
            new Animator.AnimatorPos(9, 0),
    };


    /* For Map*/
    private static final Animator.AnimatorPos[] MAP_ANIM_POS = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(0, 0),
            new Animator.AnimatorPos(224, 0),
    };


    /* For GHOST */
        /* for RED one */
    private static final Animator.AnimatorPos[] RED_RIGHT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(0, 64),
            new Animator.AnimatorPos(16, 64),
    };

    private static final Animator.AnimatorPos[] RED_LEFT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 64),
            new Animator.AnimatorPos(48, 64),
    };

    private static final Animator.AnimatorPos[] RED_UP = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(64, 64),
            new Animator.AnimatorPos(80, 64),
    };

    private static final Animator.AnimatorPos[] RED_DOWN = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(96, 64),
            new Animator.AnimatorPos(112, 64),
    };
        /* for PINK one */
    private static final Animator.AnimatorPos[] PINK_LEFT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 80),
                new Animator.AnimatorPos(48, 80),
    };

    private static final Animator.AnimatorPos[] PINK_RIGHT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(0, 80),
            new Animator.AnimatorPos(16, 80),
    };

    private static final Animator.AnimatorPos[] PINK_UP = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(64, 80),
            new Animator.AnimatorPos(80, 80),
    };

    private static final Animator.AnimatorPos[] PINK_DOWN = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(96, 80),
            new Animator.AnimatorPos(112, 80),
    };
        /* for CYAN one */

    private static final Animator.AnimatorPos[] CYAN_LEFT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 96),
            new Animator.AnimatorPos(48, 96),
    };

    private static final Animator.AnimatorPos[] CYAN_RIGHT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(0, 96),
            new Animator.AnimatorPos(16, 96),
    };

    private static final Animator.AnimatorPos[] CYAN_UP = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(64, 96),
            new Animator.AnimatorPos(80, 96),
    };

    private static final Animator.AnimatorPos[] CYAN_DOWN = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(96, 96),
            new Animator.AnimatorPos(112, 96),
    };

        /* for ORANGE one */
    private static final Animator.AnimatorPos[] ORANGE_LEFT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(32, 112),
            new Animator.AnimatorPos(48, 112),
    };

    private static final Animator.AnimatorPos[] ORANGE_RIGHT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(0, 112),
            new Animator.AnimatorPos(16, 112),
    };

    private static final Animator.AnimatorPos[] ORANGE_UP = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(64, 112),
            new Animator.AnimatorPos(80, 112),
    };

    private static final Animator.AnimatorPos[] ORANGE_DOWN = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(96, 112),
            new Animator.AnimatorPos(112, 112),
    };

        /* for FRIGHTENED one */
    private static final Animator.AnimatorPos[] LATE_FRIGHTENED = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(128, 64),
            new Animator.AnimatorPos(144, 64),
            new Animator.AnimatorPos(160, 64),
            new Animator.AnimatorPos(176, 64),
    };

    private static final Animator.AnimatorPos[] FRIGHTENED = new Animator.AnimatorPos[]{
            new Animator.AnimatorPos(128, 64),
            new Animator.AnimatorPos(144, 64),
    };


        /*  for Eaten one */
    private static final Animator.AnimatorPos[] EATEN_RIGHT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(128, 80),
    };

    private static final Animator.AnimatorPos[] EATEN_LEFT = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(144, 80),
    };

    private static final Animator.AnimatorPos[] EATEN_UP = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(160, 80),
    };

    private static final Animator.AnimatorPos[] EATEN_DOWN = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(176, 80),
    };





    /**
     * Animator for Pacman
     */
    public static final Animator PACMAN_ANIMATOR = Animator.getDirAnimator(ImageLib.SPRITE_SHEET, GameModel.PACMAN_ANIMATION_RATE, PACMAN_SIZE, PACMAN_SIZE, PACMAN_LEFT, PACMAN_RIGHT, PACMAN_UP, PACMAN_DOWN);

    public static final Animator DIED_PACMAN_ANIMATOR = Animator.getDirAnimator(ImageLib.SPRITE_SHEET, GameModel.PAC_DIED_ANIMATION_RATE, PACMAN_SIZE, PACMAN_SIZE,  DIED_PACMAN_POS, DIED_PACMAN_POS, DIED_PACMAN_POS, DIED_PACMAN_POS);

    /**
     * Animator for Energizer
     */
    public static final Animator ENERGIZER_ANIMATOR = Animator.getNonDirAnimator(ImageLib.ENERGIZER_SHEET, GameModel.ENERGIZER_BLINKING_RATE, ENERGIZER_SIZE, ENERGIZER_SIZE,  ENERGIZER_ANIM_POS);


    /**
     * Animator for Red-Ghosts
     */
    private static final Animator RED_ANIMATOR = Animator.getDirAnimator(ImageLib.SPRITE_SHEET, GameModel.GHOST_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  RED_LEFT, RED_RIGHT, RED_UP, RED_DOWN);

    /**
     * Animator for Pink-Ghosts
     */
    private static final Animator PINK_ANIMATOR = Animator.getDirAnimator(ImageLib.SPRITE_SHEET, GameModel.GHOST_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  PINK_LEFT, PINK_RIGHT, PINK_UP, PINK_DOWN);

    /**
     * Animator for Cyan-Ghosts
     */
    private static final Animator CYAN_ANIMATOR = Animator.getDirAnimator(ImageLib.SPRITE_SHEET, GameModel.GHOST_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  CYAN_LEFT, CYAN_RIGHT, CYAN_UP, CYAN_DOWN);

    /**
     * Animator for Orange-Ghosts
     */
    private static final Animator ORANGE_ANIMATOR = Animator.getDirAnimator(ImageLib.SPRITE_SHEET, GameModel.GHOST_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  ORANGE_LEFT, ORANGE_RIGHT, ORANGE_UP, ORANGE_DOWN);

    /**
     * Animator for Frightened-Ghosts
     */
    private static final Animator LATE_FRIGHTENED_ANIMATOR = Animator.getDirAnimator(ImageLib.SPRITE_SHEET, GameModel.GHOST_FRIGHTENED_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  LATE_FRIGHTENED, LATE_FRIGHTENED, LATE_FRIGHTENED, LATE_FRIGHTENED);

    private static final Animator FRIGHTENED_ANIMATOR = Animator.getDirAnimator(ImageLib.SPRITE_SHEET, GameModel.GHOST_FRIGHTENED_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  FRIGHTENED, FRIGHTENED, FRIGHTENED, FRIGHTENED);

    /**
     * Animator for Eaten-Ghosts
     */
    private static final Animator EATEN_ANIMATOR = Animator.getDirAnimator(ImageLib.SPRITE_SHEET, GameModel.GHOST_FRIGHTENED_ANIMATION_RATE, GHOST_SIZE, GHOST_SIZE,  EATEN_LEFT, EATEN_RIGHT, EATEN_UP, EATEN_DOWN);


    /**
     * Animator for Map
     */
    public static final Animator MAP_ANIMATOR = Animator.getNonDirAnimator(ImageLib.MAP_SHEET, MAP_FLASHING_RATE, Global.ORIGINAL_TILE_SIZE * 28, Global.ORIGINAL_TILE_SIZE * 31,  MAP_ANIM_POS);

    /**
     * Get animator for ghost, index is one of
     * {@link GameModel#RED_GHOST},
     * {@link GameModel#PINK_GHOST},
     * {@link GameModel#CYAN_GHOST},
     * {@link GameModel#ORANGE_GHOST},
     * {@link GameModel#FRIGHTENED_GHOST}
     */
    public static final Animator[] GHOST_ANIMATOR = {RED_ANIMATOR, PINK_ANIMATOR, CYAN_ANIMATOR, ORANGE_ANIMATOR, FRIGHTENED_ANIMATOR, LATE_FRIGHTENED_ANIMATOR, EATEN_ANIMATOR};


}
