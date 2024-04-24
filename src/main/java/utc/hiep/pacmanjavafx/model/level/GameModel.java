package utc.hiep.pacmanjavafx.model.level;

import static utc.hiep.pacmanjavafx.lib.Global.GAME_SCALE;

public interface GameModel {
    byte RED_GHOST    = 0;
    byte PINK_GHOST   = 1;
    byte CYAN_GHOST   = 2;
    byte ORANGE_GHOST = 3;

    /** Game loop frequency. */
    short FPS = 60;

    short PAC_POWER_FADING_TICKS = 2 * FPS;
    short BONUS_POINTS_SHOWN_TICKS = 2 * FPS;
    /** Base speed of creatures (Pac-Man, ghosts, moving bonus). */
    short PPS_AT_100_PERCENT = 75 * GAME_SCALE;
    short PPS_GHOST_INHOUSE = 38;
    short PPS_GHOST_RETURNING_HOME = 150;
    short POINTS_NORMAL_PELLET = 10;
    short POINTS_ENERGIZER = 50;
    short POINTS_ALL_GHOSTS_KILLED_IN_LEVEL = 12_000;
    short EXTRA_LIFE_SCORE = 10_000;
    byte LEVEL_COUNTER_MAX_SYMBOLS = 7;
    byte PACMAN_ANIMATION_RATE = 3;
    byte ENERGIZER_BLINKING_RATE = 30;

    byte PELLET_POINT = 10;
    byte ENERGIZER_POINT = 50;
}
