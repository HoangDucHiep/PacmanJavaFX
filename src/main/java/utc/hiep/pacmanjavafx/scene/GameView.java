package utc.hiep.pacmanjavafx.scene;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utc.hiep.pacmanjavafx.lib.*;
import utc.hiep.pacmanjavafx.model.entity.Ghost;
import utc.hiep.pacmanjavafx.model.entity.Pacman;
import utc.hiep.pacmanjavafx.model.level.GameLevel;
import utc.hiep.pacmanjavafx.model.world.PacmanMap;
import utc.hiep.pacmanjavafx.model.world.World;

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


        pacman.render(gc);

        for (Ghost ghost : ghosts) {
            ghost.render(gc);
        }
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
    private final Font textFont = FontLib.EMULOGIC(5);
    private void drawTargetTile(int ghostID, Color color) {
        if(ghosts[ghostID].targetTile().isEmpty()) return;
        iVector2D target = ghosts[ghostID].targetTile().get();
        gc.setFill(color);
        gc.setFont(targetFont);
        gc.fillText("X", target.x()*TILE_SIZE + 1.5, target.y()*TILE_SIZE + 13);
        gc.setFont(textFont);
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

            if (gameLevel.currentChasingTargetPhaseName().equals(GameLevel.CHASING)) {
                double centerX = ghosts[ORANGE_GHOST].posX();
                double centerY = ghosts[ORANGE_GHOST].posY();
                double radius = 8 * TILE_SIZE;
                int numDashes = 30;

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

                    if (target.equals(PacmanMap.SCATTER_TARGET_LEFT_LOWER_CORNER)) {
                        gc.setStroke(Color.color(1, 0.71, 0.31, 0.3));
                    } else {
                        gc.setStroke(Color.color(1, 0.71, 0.31, 0.8));
                    }

                    gc.strokeLine(startX, startY, endX, endY);
                }


                drawTargetTile(ORANGE_GHOST, Color.ORANGE);
            }
        }
    }

}

