package pumpkinbox.games.checkers;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

enum PieceType {
    BLACK(1), WHITE(-1);
    int moveDir;
    PieceType(int moveDir) {
        this.moveDir = moveDir;
    }
    boolean isKing = false;
    void makeKing() { isKing = true;}
}

public class Piece extends StackPane {
    public static final int TILE_SIZE = 100;

    public PieceType type;

    private double mouseX, mouseY;
    private double oldX, oldY;

    public PieceType getType() {
        return type;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public Piece(Piece pieceToCopy) {
        if (pieceToCopy != null) {
            type = pieceToCopy.type;
        }
//        oldX = pieceToCopy.oldX; oldY = pieceToCopy.oldY;
    }

    public Piece(PieceType type, int x, int y) {
        this.type = type;

        move(x, y);

        Ellipse bg = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        bg.setFill(Color.BLACK);

        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(TILE_SIZE * 0.03);

        bg.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        bg.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2 + TILE_SIZE * 0.07);

        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        ellipse.setFill(type == PieceType.BLACK
                ? Color.valueOf("#472f0a") : Color.valueOf("#f7c98c"));

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);

        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

        getChildren().addAll(bg, ellipse);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }

    public void move(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    public void makeKing() {
        // draw a cirlce on the piece and make it a king
        Ellipse bg = new Ellipse(TILE_SIZE * 0.052, TILE_SIZE * 0.052);
        bg.setFill(Color.YELLOW);

        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(TILE_SIZE * 0.01);
        bg.setTranslateX((TILE_SIZE - TILE_SIZE * 0.32 * 2) / 2);
        bg.setTranslateY((TILE_SIZE - TILE_SIZE * 0.31 * 2) / 2 + TILE_SIZE * 0.07);

        getChildren().add(bg);

        type.makeKing();
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }
}
