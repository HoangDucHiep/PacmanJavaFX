package utc.hiep.pacmanjavafx.model.level;

import utc.hiep.pacmanjavafx.model.DatabaseControl;
import utc.hiep.pacmanjavafx.model.HUD;

import static utc.hiep.pacmanjavafx.lib.Global.GAME_SCALE;

public interface GameModel {
        byte RED_GHOST    = 0;
        byte PINK_GHOST   = 1;
        byte CYAN_GHOST   = 2;
        byte ORANGE_GHOST = 3;
        byte FRIGHTENED_GHOST = 4;  //use only for animation
        byte LATE_FRIGTHENED_GHOST = 5;  //use only for animation
        byte EATEN_GHOST = 6;       //use only for animation
        byte PAC = 7;
        byte DIED_PAC = 8;
        byte MAP = 9;
        byte ENERGIZEER = 10;

        String[] GHOST_NAME = {"Blinky", "Pinky", "Inky", "Clyde"};

        /** Game loop frequency. */
        short FPS = 60;

        /** Base speed of creatures (Pac-Man, ghosts, moving bonus). */
        short PPS_AT_100_PERCENT = 88 * GAME_SCALE;
        short PPS_GHOST_INHOUSE = 38 * GAME_SCALE;
        short PPS_GHOST_RETURNING_HOME = 250 * GAME_SCALE;

        short POINTS_NORMAL_PELLET = 10;
        short POINTS_ENERGIZER = 50;
        short EXTRA_LIFE_SCORE = 10_000;

        byte PACMAN_ANIMATION_RATE = 3;
        byte ENERGIZER_BLINKING_RATE = 30;
        byte GHOST_ANIMATION_RATE = 6;
        byte GHOST_FRIGHTENED_ANIMATION_RATE = 15;
        byte PAC_DIED_ANIMATION_RATE = 6;
        short PAC_DIED_ANIMATION_LENGTH = 14 * PAC_DIED_ANIMATION_RATE;
        byte MAP_FLASHING_RATE = 20;

        //phase name
        String SCATTERING = "Scattering";
        String CHASING = "Chasing";

        byte MAX_LEVEL = 21;

        byte[][] RAW_LEVEL_DATA = {
                /* 1*/ { 80, 75, 40,  20,  80, 10,  85,  90, 50, 5},
                /* 2*/ { 90, 85, 45,  30,  90, 15,  95,  95, 55, 5},
                /* 3*/ { 90, 85, 45,  40,  90, 20,  95,  95, 55, 10},
                /* 4*/ { 90, 85, 45,  40,  90, 20,  95,  95, 55, 10},
                /* 5*/ {100, 95, 50,  40, 100, 20, 105, 100, 60, 10},
                /* 6*/ {100, 95, 50,  50, 100, 25, 105, 100, 60, 20},
                /* 7*/ {100, 95, 50,  50, 100, 25, 105, 100, 60, 20},
                /* 8*/ {100, 95, 50,  50, 100, 25, 105, 100, 60, 20},
                /* 9*/ {100, 95, 50,  60, 100, 30, 105, 100, 60, 20},
                /*10*/ {100, 95, 50,  60, 100, 30, 105, 100, 60, 20},
                /*11*/ {100, 95, 50,  60, 100, 30, 105, 100, 60, 20},
                /*12*/ {100, 95, 50,  80, 100, 40, 105, 100, 60, 50},
                /*13*/ {100, 95, 50,  80, 100, 40, 105, 100, 60, 50},
                /*14*/ {100, 95, 50,  80, 100, 40, 105, 100, 60, 50},
                /*15*/ {100, 95, 50, 100, 100, 50, 105, 100, 60, 50},
                /*16*/ {100, 95, 50, 100, 100, 50, 105, 100, 60, 50},
                /*17*/ {100, 95, 50, 100, 100, 50, 105,   0,  0, 50},
                /*18*/ {100, 95, 50, 100, 100, 50, 105, 100, 60, 100},
                /*19*/ {100, 95, 50, 120, 100, 60, 105,   0,  0, 100},
                /*20*/ {100, 95, 50, 120, 100, 60, 105,   0,  0, 100},
                /*21*/ { 90, 95, 50, 120, 100, 60, 105,   0,  0, 100},
        };


        // Hunting duration (in ticks) of chase and scatter phases. See Pac-Man dossier.
        final int[][] CHASING_TARGET_DURATIONS = {
                {7 * FPS, 20 * FPS, 7 * FPS, 20 * FPS, 5 * FPS,   20 * FPS, 5 * FPS, -1}, // Level 1
                {7 * FPS, 20 * FPS, 7 * FPS, 20 * FPS, 5 * FPS, 1033 * FPS,       1, -1}, // Levels 2-4
                {5 * FPS, 20 * FPS, 5 * FPS, 20 * FPS, 5 * FPS, 1037 * FPS,       1, -1}, // Levels 5+
        };


        final int[] FRIGHTENED_DURATION = {6 * FPS, 5 * FPS, 4 * FPS, 3 * FPS, 2 * FPS, 5 * FPS, 2 * FPS, 2 * FPS, FPS, 5 * FPS, 2 * FPS, FPS, FPS, 3 * FPS, FPS};

        static int chasingTargetDuration(int level, long huntingPhaseIndex) {
            return CHASING_TARGET_DURATIONS[level == 1 ? 0 : level <= 4 ? 1 : 2][(int) huntingPhaseIndex];
        }

        static int frightenedDuration(int level) {
            return FRIGHTENED_DURATION[level < 15 ? level - 1 : 14];
        }

        static String ghostName(byte id) {
            return GHOST_NAME[id];
        }


        public void initNewGame();

        long score();

        int lives();

        void addScore(int points);

        void removeLife();

        void addLife();

        GameLevel gameLevel();

        long highScore();

        int highLevel();

        HUD hud();

        DatabaseControl db();

}
