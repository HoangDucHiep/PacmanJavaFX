package utc.hiep.pacmanjavafx.scene;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utc.hiep.pacmanjavafx.event.GameEvent;
import utc.hiep.pacmanjavafx.lib.*;
import utc.hiep.pacmanjavafx.model.HUD;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.level.GameLevel;
import utc.hiep.pacmanjavafx.model.level.LevelState;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

import static utc.hiep.pacmanjavafx.lib.Global.*;
import static utc.hiep.pacmanjavafx.model.level.GameModel.*;

public class GameView extends GeneralScene {
    public static final int GAME_WIDTH = TILE_SIZE * TILES_X;   //all size is pixel
    public static final int GAME_HEIGHT = TILE_SIZE * TILES_Y;

    private boolean isGridDisplayed = false;
    private Font eventTextFont = FontLib.EMULOGIC(TILE_SIZE);

    private Canvas canvas;
    private GraphicsContext gc;


    //Game entity
    private World world;
    private Pacman pacman;
    private Ghost[] ghosts;
    private GameLevel gameLevel;

    private HUD hud;


    /**
     * Constructor
     */
    public GameView(HUD hud) {
        super();
        setBackGround();

        //Border around canvas
        Region rectangle = new Region();
        rectangle.setMaxWidth(GAME_WIDTH * 1.15);
        rectangle.setMaxHeight(GAME_HEIGHT * 1.15);
        rectangle.setMinWidth(GAME_WIDTH * 1.15);
        rectangle.setMinHeight(GAME_HEIGHT * 1.15);
        rectangle.setStyle("-fx-background-color: white, black; -fx-background-insets: 0, 12; -fx-background-radius: 10px;");
        getRootPane().getChildren().add(rectangle);

        //Canvas Init
        canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        getRootPane().getChildren().add(canvas);

        this.hud = hud;
    }


    public void setGameLevel(GameLevel gameLevel) {
        this.gameLevel = gameLevel;
        this.pacman = gameLevel.pacman();
        this.ghosts = gameLevel.ghosts();
        this.world = gameLevel.world();
    }


    /**
     * Set background for main pane
     */
    private void setBackGround() {
        BackgroundImage background = new BackgroundImage(ImageLib.BACKGROUND_IMAGE, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
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
            drawGhostTarget();
        }

        if(pacman.isVisible())
            pacman.render(gc);

        for (Ghost ghost : ghosts) {
            if(ghost.isVisible())
                ghost.render(gc);
        }

        if(gameLevel.currentState() == LevelState.LEVEL_READY) {
            gc.setFill(Color.YELLOW);
            gc.setFont(eventTextFont);
            gc.fillText("READY!", 11 * TILE_SIZE, 21 * TILE_SIZE);
        } else if(gameLevel.currentEvent() == GameEvent.GAME_OVER) {
            gc.setFill(Color.RED);
            gc.setFont(eventTextFont);
            gc.fillText("GAME OVER", 10 * TILE_SIZE - HALF_TILE_SIZE, 21 * TILE_SIZE);
        }

        hud.render(gc);
    }




    //Use only for testing things
    public void switchGridDisplay() {
        isGridDisplayed = !isGridDisplayed;
    }

    private void drawTileSystem() {
        for (int i = 0; i < TILES_X; i++) {
            for (int j = 0; j < TILES_Y; j++) {
                gc.setStroke(Color.color(0.12, 0.23, 0.12, 0.4));
                gc.strokeRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private final Font targetFont = FontLib.EMULOGIC(12);
    private final Font targetTextFont = FontLib.EMULOGIC(5);
    private void drawTargetTile(int ghostID, Color color) {
        if(ghosts[ghostID].targetTile().isEmpty()) return;
        iVector2D target = ghosts[ghostID].targetTile().get();
        gc.setFill(color);
        gc.setFont(targetFont);
        gc.fillText("X", target.x()*TILE_SIZE + 1.5, target.y()*TILE_SIZE + 13);
        gc.setFont(targetTextFont);
        if(ghostID == RED_GHOST || ghostID == PINK_GHOST)
            gc.fillText("(Target)", (target.x() - 0.75) * TILE_SIZE, (target.y() + 2) * TILE_SIZE);
        else
            gc.fillText("(Target)", (target.x() - 0.75) * TILE_SIZE, (target.y() - 0.5) * TILE_SIZE);
    }
    public void drawGhostTarget() {
        //Draw portal
        //red

        if (ghosts[RED_GHOST].targetTile().isPresent()) {
            drawTargetTile(RED_GHOST, Color.RED);
        }

        if (ghosts[PINK_GHOST].targetTile().isPresent()) {
            drawTargetTile(PINK_GHOST, Color.color(1, 0.71, 1));
        }

        if (ghosts[CYAN_GHOST].targetTile().isPresent()) if(ghosts[PINK_GHOST].targetTile().isEmpty()) return;
        {
            drawTargetTile(CYAN_GHOST, Color.CYAN);
        }

        if (ghosts[ORANGE_GHOST].targetTile().isPresent()) {
            if(ghosts[ORANGE_GHOST].targetTile().isEmpty()) return;
            iVector2D target = ghosts[ORANGE_GHOST].targetTile().get();
            drawTargetTile(ORANGE_GHOST, Color.ORANGE);
            if (gameLevel.currentChasingTargetPhaseName().equals(CHASING)) {
                double centerX = ghosts[ORANGE_GHOST].posX();
                double centerY = ghosts[ORANGE_GHOST].posY();
                double radius = 8 * TILE_SIZE;
                int numDashes = 20;

                double dashAngle = 360.0 / numDashes;

                if (target.equals(PacmanMap.SCATTER_TARGET_LEFT_LOWER_CORNER)) {
                    gc.setStroke(Color.color(1, 0.71, 0.31, 0.3));
                } else {
                    gc.setStroke(Color.color(1, 0.71, 0.31, 0.8));
                }
                for (int i = 0; i < numDashes; i++) {
                    double startAngle = i * dashAngle;
                    double endAngle = (i + 0.5) * dashAngle;

                    startAngle = Math.toRadians(startAngle);
                    endAngle = Math.toRadians(endAngle);

                    double startX = centerX + radius * Math.cos(startAngle);
                    double startY = centerY + radius * Math.sin(startAngle);
                    double endX = centerX + radius * Math.cos(endAngle);
                    double endY = centerY + radius * Math.sin(endAngle);
                    gc.strokeLine(startX, startY, endX, endY);
                }
            }
        }
    }

}

