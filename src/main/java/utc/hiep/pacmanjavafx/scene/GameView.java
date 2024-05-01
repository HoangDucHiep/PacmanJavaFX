package utc.hiep.pacmanjavafx.scene;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import utc.hiep.pacmanjavafx.lib.AnimatorLib;
import utc.hiep.pacmanjavafx.lib.ImageLibrary;
import utc.hiep.pacmanjavafx.lib.iVector2D;
import utc.hiep.pacmanjavafx.model.Animator;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.level.GameLevel;
import utc.hiep.pacmanjavafx.model.level.GameModel;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

import java.util.function.Consumer;

import static utc.hiep.pacmanjavafx.lib.Global.*;
import static utc.hiep.pacmanjavafx.model.level.GameModel.*;

public class GameView extends GeneralScene {
    public static final int GAME_WIDTH = TILE_SIZE * TILES_X;   //all size is pixel
    public static final int GAME_HEIGHT = TILE_SIZE * TILES_Y;

    private boolean isGridDisplayed = false;

    private Canvas canvas;
    private GraphicsContext gc;


    //Game entity
    private World world;
    private Pacman pacman;
    private Ghost[] ghosts;
    private GameLevel gameLevel;


    /**
     * Constructor
     */
    public GameView() {
        super();
        setBackGround();

        //Border around canvas
        Region rectangle = new Region();
        rectangle.setStyle("-fx-background-color: white, black; -fx-background-insets: 0, 12; -fx-background-radius: 10px; -fx-min-width: 520; -fx-min-height:669; -fx-max-width:480; -fx-max-height: 669;");
        getRootPane().getChildren().add(rectangle);

        //Canvas Init
        canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getRootPane().getChildren().add(canvas);

    }

    public void setGameEntity(Pacman pacman, World world, Ghost[] ghosts) {
        this.pacman = pacman;
        this.world = world;
        this.ghosts = ghosts;
    }

    public void setGameLevel(GameLevel gameLevel) {
        this.gameLevel = gameLevel;
    }


    /**
     * Set background for main pane
     */
    private void setBackGround() {
        BackgroundImage background = new BackgroundImage(ImageLibrary.BACKGROUND_IMAGE, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        getRootPane().setBackground(new Background(background));
    }


    /**
     * Drawing game each pulse
     */
    @Override
    public void render() {
        gc.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT); //clear last frame rendering


        world.drawMap(gc);
        if (isGridDisplayed) {
            drawTileSystem();
        }

        pacman.render(gc);

        if (isGridDisplayed) {
            drawGhostTarget();
        }
        for (Ghost ghost : ghosts) {
            ghost.render(gc);
        }
    }


    public void switchGridDisplay() {
        isGridDisplayed = !isGridDisplayed;
    }


    //Use only for testing things
    private void drawTileSystem() {
        for (int i = 0; i < TILES_X; i++) {
            for (int j = 0; j < TILES_Y; j++) {
                gc.setStroke(Color.color(0.12, 0.23, 0.12));
                gc.strokeRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }


    }

    public void drawGhostTarget() {
        //Draw portal
        //red
        if (ghosts[RED_GHOST].targetTile().isPresent()) {
            gc.setStroke(Color.RED);
            gc.strokeRect((ghosts[RED_GHOST].targetTile().get().x() * TILE_SIZE) + 3, (ghosts[RED_GHOST].targetTile().get().y() * TILE_SIZE) + 2, TILE_SIZE, TILE_SIZE);
        }

        if (ghosts[PINK_GHOST].targetTile().isPresent()) {
            gc.setStroke(Color.HOTPINK);
            gc.strokeRect(ghosts[PINK_GHOST].targetTile().get().x() * TILE_SIZE, ghosts[PINK_GHOST].targetTile().get().y() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        if (ghosts[CYAN_GHOST].targetTile().isPresent()) {

            gc.setStroke(Color.CYAN);
            gc.strokeRect(ghosts[CYAN_GHOST].targetTile().get().x() * TILE_SIZE, ghosts[CYAN_GHOST].targetTile().get().y() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        if (ghosts[ORANGE_GHOST].targetTile().isPresent()) {
            gc.setStroke(Color.ORANGE);
            gc.strokeRect(ghosts[ORANGE_GHOST].targetTile().get().x() * TILE_SIZE, ghosts[ORANGE_GHOST].targetTile().get().y() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            iVector2D target = ghosts[ORANGE_GHOST].targetTile().get();
            if (gameLevel.currentChasingTargetPhaseName().equals(GameLevel.CHASING)) {
                double centerX = ghosts[ORANGE_GHOST].posX();
                double centerY = ghosts[ORANGE_GHOST].posY();
                double radius = 8 * TILE_SIZE;
                int numDashes = 50;

                double dashAngle = 360.0 / numDashes;

                for (int i = 0; i < numDashes; i++) {
                    double startAngle = i * dashAngle;
                    double endAngle = (i + 0.5) * dashAngle;

                    startAngle = Math.toRadians(startAngle);
                    endAngle = Math.toRadians(endAngle);

                    double startX = centerX + radius * Math.cos(startAngle);
                    double startY = centerY + radius * Math.sin(startAngle);
                    double endX = centerX + radius * Math.cos(endAngle);
                    double endY = centerY + radius * Math.sin(endAngle);

                    if(target.equals(PacmanMap.SCATTER_TARGET_LEFT_LOWER_CORNER)) {
                        gc.setStroke(Color.color(0.66, 0.26, 0.07));
                    }

                    gc.setLineWidth(2);
                    gc.strokeLine(startX, startY, endX, endY);
                }
            }
        }
    }
}
