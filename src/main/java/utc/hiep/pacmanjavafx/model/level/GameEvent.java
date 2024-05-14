package utc.hiep.pacmanjavafx.model.level;

public enum GameEvent {
    NONE,
    GHOST_EATEN,    //timer
    PAC_DIED,    //pacdie
    PAC_FOUND_FOOD,
    PAC_EAT_ENERGIZER,
    GAME_OVER,      //timer
    GAME_WIN        //timer
}