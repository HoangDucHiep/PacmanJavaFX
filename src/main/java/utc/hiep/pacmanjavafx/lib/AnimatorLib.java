package utc.hiep.pacmanjavafx.lib;

import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.level.GameModel;

public class AnimatorLib {

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
            new Animator.AnimatorPos(0, 48),
            new Animator.AnimatorPos(0, 48),
            new Animator.AnimatorPos(16, 48),
            new Animator.AnimatorPos(32, 0)
    };

    /* For energizer */
    private static final Animator.AnimatorPos[] ENERGIZER_ANIM_POS = new Animator.AnimatorPos[] {
            new Animator.AnimatorPos(0, 0),
            new Animator.AnimatorPos(9, 0),
    };

    /**
     * Animator for pacman
     */
    public static final Animator PACMAN_ANIMATOR = Animator.getDirAnimator(GameModel.PACMAN_ANIMATION_RATE, PACMAN_LEFT, PACMAN_RIGHT, PACMAN_UP, PACMAN_DOWN);

    public static final Animator ENERGIZER_ANIMATOR = Animator.getNonDirAnimator(GameModel.ENERGIZER_BLINKING_RATE, ENERGIZER_ANIM_POS);

}
