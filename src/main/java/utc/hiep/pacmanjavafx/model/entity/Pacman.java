package utc.hiep.pacmanjavafx.model.entity;

import javafx.scene.canvas.GraphicsContext;
import utc.hiep.pacmanjavafx.controller.GameController;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.level.GameModel;
import utc.hiep.pacmanjavafx.model.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utc.hiep.pacmanjavafx.lib.Global.*;

public class Pacman extends MovableEntity{
    private final String name;
    private long starvingTicks;
    private final List<Ghost> victims;      //list of ghosts that pacman can eat at the moment


    public Pacman(String name) {
        super();
        checkNotNull(name);
        reset();
        this.name = name;
        this.starvingTicks = 0;
        this.victims = new ArrayList<>();

        //set animation sprite ui
        animator = GameController.rm().getAnimator(GameModel.PAC);
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean canTurnBack() {
        return true;
    }

    @Override
    public boolean canAccessTile(iVector2D tile, World world) {
        if (world.house().contains(tile)) {
            return false;
        }
        if (world.insideBounds(tile)) {
            return !world.isWall(tile);
        }
        return world.belongsToPortal(tile);
    }

    public long starvingTicks() {
        return starvingTicks;
    }

    public void starve() {
        ++starvingTicks;
    }

    public void endStarving() {
        starvingTicks = 0;
    }


    @Override
    public void render(GraphicsContext gc) {
        animator.render(gc, posX() - HALF_TILE_SIZE, posY() - HALF_TILE_SIZE);
    }

    public void setVictims(Ghost[] ghosts) {
        victims.clear();
        Collections.addAll(victims, ghosts);
        System.out.println(victims);
    }

    public List<Ghost> victims() {
        if(victims.isEmpty()) {
            return Collections.emptyList();
        }
        return victims;
    }
}
