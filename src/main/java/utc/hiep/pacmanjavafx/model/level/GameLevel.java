package utc.hiep.pacmanjavafx.model.level;

import utc.hiep.pacmanjavafx.event.KeyListener;
import utc.hiep.pacmanjavafx.event.KeyType;
import utc.hiep.pacmanjavafx.lib.Direction;
import utc.hiep.pacmanjavafx.lib.Vector2f;
import utc.hiep.pacmanjavafx.lib.Vector2i;
import utc.hiep.pacmanjavafx.model.Timer;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.world.World;

import java.util.List;

public class GameLevel implements GameModel{
    public record Data(
            int number, // Level number, starts at 1.
            boolean demoLevel,
            byte pacSpeedPercentage, // Relative Pac-Man speed (percentage of base speed).
            byte ghostSpeedPercentage, // Relative ghost speed when hunting or scattering.
            byte ghostSpeedTunnelPercentage, // Relative ghost speed inside tunnel.
            byte elroy1DotsLeft,//  Number of pellets left when Blinky becomes "Cruise Elroy" grade 1.
            byte elroy1SpeedPercentage, // Relative speed of Blinky being "Cruise Elroy" grade 1.
            byte elroy2DotsLeft, // Number of pellets left when Blinky becomes "Cruise Elroy" grade 2.
            byte elroy2SpeedPercentage, //Relative speed of Blinky being "Cruise Elroy" grade 2.
            byte pacSpeedPoweredPercentage, // Relative speed of Pac-Man in power mode.
            byte ghostSpeedFrightenedPercentage, // Relative speed of frightened ghost.
            byte pacPowerSeconds, // Number of seconds Pac-Man gets power.
            byte numFlashes, // Number of maze flashes at end of this level.
            byte intermissionNumber) // Number (1,2,3) of intermission scene played after this level (0=no intermission).
    {
        public Data(int number, boolean demoLevel, byte[] data) {
            this(number, demoLevel, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8],
                    data[9], data[10], data[11]);
        }
    }

    private KeyListener kl;

    private final Data data;
    private final Timer huntingTimer = new Timer();
    private final World map;
    private final Pacman pacman;
    //private final Ghost[] ghosts;
    //private final GhostHouseControl houseControl;
    //private final List<Byte> bonusSymbols;
    //private Bonus bonus;
    //private byte huntingPhaseIndex;
    //private byte totalNumGhostsKilled;
    //private byte cruiseElroyState;
    //private SimulationStepEventLog eventLog;
    //private byte bonusReachedIndex; // -1=no bonus, 0=first, 1=second

    public GameLevel(Data data, World map, Pacman pacman, KeyListener kl) {
        this.data = data;
        this.map = map;
        this.pacman = pacman;
        this.kl = kl;
    }

    public void update() {
        keyHandler();
        updateAnimator();
        movePacman();
        handlePacmanEatFoot();
    }


    public void movePacman() {
        Vector2i currentTile = pacman.atTile();

        /* Handle turn back instantly */
        if(pacman.movingDir().opposite().equals(pacman.nextDir())) {
            pacman.turnBackInstantly();
            return;
        }

        /*  handle pacman be blocked by wall or smth */
        if(!pacman.canAccessTile(pacman.tilesAhead(1), map) && pacman.offset().almostEquals(Vector2f.ZERO, pacman.currentSpeed(),  pacman.currentSpeed())) {
            if(!pacman.isStanding()) {
                pacman.placeAtTile(currentTile.toFloatVec());
                pacman.standing();
            }
        }
        /*  handle pacman at intersection */
        else if(map.isIntersection(currentTile)) {
            //if pacman haven't aligned to tile, but almost aligned, then aligned it
            if(pacman.isNewTileEntered() && pacman.offset().almostEquals(Vector2f.ZERO, pacman.currentSpeed(), pacman.currentSpeed())) {
                pacman.placeAtTile(currentTile.toFloatVec());
            }
        }
        //Handle if pacman gothrough portal
        else if(map.belongsToPortal(currentTile)) {
            if(!map.belongsToPortal(pacman.tilesAhead(1))) {
                Vector2i teleportTo = map.portals().otherTunnel(currentTile);
                pacman.placeAtTile(teleportTo.toFloatVec());
            }
        }

        /* Handle if pacman be blocked in next turn, it'll keep moving in current direction*/
        if(pacman.isAlignedToTile()) {
            if(pacman.canAccessTile(currentTile.plus(pacman.nextDir().vector()), map)) {
                pacman.setMovingDir(pacman.nextDir());
            }
        }


        // If pacman is not standing, it can move :)))
        if(!pacman.isStanding()) {
            pacman.move();
        }
    }

    public void updateAnimator() {
        pacman.animatorUpdate();
        map.animatorUpdate();
    }

    private void handlePacmanEatFoot() {
        Vector2i currentTile = pacman.atTile();
        if(map.hasFoodAt(currentTile) && !map.hasEatenFoodAt(currentTile)) {
            map.removeFood(currentTile);
        }
    }


    public void keyHandler() {
        for (KeyType key : kl.getPressedKey()) {
            switch (key) {
                case TURN_UP -> pacman.setNextDir(Direction.UP);
                case TURN_DOWN -> pacman.setNextDir(Direction.DOWN);
                case TURN_LEFT -> pacman.setNextDir(Direction.LEFT);
                case TURN_RIGHT -> pacman.setNextDir(Direction.RIGHT);
            }
        }
        kl.clearKey();
    }


}
