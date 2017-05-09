package pumpkinbox.games.checkers;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import pumpkinbox.client.GameClient;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Checkers extends Application {

    HBox statusbar = new HBox();
    Label statusLabel = new Label();

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private static int sender_id;
    private static int receiver_id;

    Tile[][] board = new Tile[WIDTH][HEIGHT];
    int roundNb = 0;
    int nbWhitePieces = 12, nbBlackPieces = 12, nbWhiteKings = 0, nbBlackKings = 0;

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();

    int PieceX, PieceY;
    boolean waitSecondKill = false;
    PieceType currentPlayer = PieceType.WHITE;
    String move;

    static boolean isOffline;
    boolean isDemo = false;       // PARAMETERS
    // demo lets the player control both sides successively; leave it false

    public void setOffline(boolean offline){
    	this.isOffline = offline;
    }

    private Parent createContent() {
        BorderPane root = new BorderPane();

        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE + 19);
        root.getChildren().addAll(tileGroup, pieceGroup);


        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.BLACK, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.WHITE, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }

        statusLabel.setText("Welcome");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        statusbar.getChildren().add(statusLabel);
        root.setBottom(statusbar);

        return root;
    }

    private MoveResult tryMove(int oldX, int oldY, int newX, int newY) {
        Piece piece = board[oldX][oldY].getPiece();

        if (newX > 7 || newX < 0 || newY > 7 || newY < 0)
            return new MoveResult(MoveType.NONE);

        if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0)
            return new MoveResult(MoveType.NONE);

        if (Math.abs(newX - oldX) == 1 && (newY - oldY == piece.getType().moveDir || piece.type.isKing)) {
            // if the piece is a king it can move in both directions
            return new MoveResult(MoveType.NORMAL);
        } else if (Math.abs(newX - oldX) == 2 && (newY - oldY == piece.getType().moveDir * 2 || piece.type.isKing)) {

            int x1 = oldX + (newX - oldX) / 2;
            int y1 = oldY + (newY - oldY) / 2;

            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    private int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());

        isOffline = Boolean.valueOf(this.getParameters().getRaw().get(0));

        primaryStage.setTitle("Checkers");
        statusLabel.setText("Your turn to move");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            if (piece.getType() == PieceType.BLACK && isDemo == false) {
                piece.abortMove();
                return;
            } else if (currentPlayer != piece.getType()) {
                statusLabel.setText("Not your turn to move. Waiting opponent to play");
                piece.abortMove();
                return;
            }

            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());
            int oldX = toBoard(piece.getOldX());
            int oldY = toBoard(piece.getOldY());

            boolean successfullyMoved = movePiece(oldX, oldY, newX, newY);

            if (nbBlackPieces == 0) {
                statusLabel.setText("You win!");
            } else if (nbWhitePieces == 0) {
                statusLabel.setText("You lose!");
            }

            else if (currentPlayer == PieceType.WHITE && successfullyMoved) {
                sendMove(oldX, oldY, newX, newY);

                if (!isOffline) statusLabel.setText("Opponent's turn to move");
                currentPlayer = PieceType.BLACK;

                if (!isDemo)
                    receiveMove();
            } else if (currentPlayer == PieceType.BLACK && successfullyMoved) {
                statusLabel.setText("Your turn to move");
                currentPlayer = PieceType.WHITE;
            }

        });

        return piece;
    }

    void sendMove(int oldX, int oldY, int newX, int newY) {
        roundNb++;

        if (isOffline)
            return;

        // TODO: send move
        //send move
        GameClient.sendGameMove(sender_id, receiver_id, "tictactoe", Integer.toString(oldX) + " " + Integer.toString(oldY)
         + " " + Integer.toString(newX) + " " + Integer.toString(newY));


    }

    void receiveMove() {
        roundNb++;
        if (isOffline) {
            AI OpponentAI = new AI(board, nbWhitePieces, nbBlackPieces, nbWhiteKings, nbWhitePieces);
            move = OpponentAI.getMove();

//            if (roundNb == 2)
//                move = "1 2 0 3";
//            else move = "0 3 2 5";

            String[] moveArr = move.split(" ");
            movePiece(Integer.parseInt(moveArr[0]), Integer.parseInt(moveArr[1]), Integer.parseInt(moveArr[2]),
                    Integer.parseInt(moveArr[3]));
            currentPlayer = PieceType.WHITE;

            return;
        }


        Runnable r = (() -> {

            move =  GameClient.receiveGameMove(sender_id, receiver_id, "tictactoe");

            String[] oppMove = move.split(" ");

            // because we are changing the gui
            Platform.runLater(() -> {
                movePiece(Integer.parseInt(oppMove[0]), Integer.parseInt(oppMove[1]), Integer.parseInt(oppMove[2]),
                        Integer.parseInt(oppMove[3]));   // show the opponent's move

                statusLabel.setText("Your turn to move");
                currentPlayer = PieceType.WHITE;
            });
        });
        ExecutorService executor = Executors.newCachedThreadPool(); // use a thread pool because we need to spawn the thread repeatedly
        executor.submit(r);
        //this line will execute immediately, not waiting for the task to complete
    }


    public boolean movePiece(int oldX, int oldY, int newX, int newY) {
        MoveResult result;
        System.out.println("move from " + oldX + " " + oldY + " to " + newX + " " + newY);
        Piece piece = board[oldX][oldY].getPiece();

        if (waitSecondKill && !(oldX == PieceX && oldY == PieceY)) {
            piece.abortMove();
            return false;
        }


        if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
            result = new MoveResult(MoveType.NONE);
        } else {
            result = tryMove(oldX, oldY, newX, newY);
        }

        if (newY == 0 && piece.getType() == PieceType.WHITE) {
            nbWhiteKings++;
            piece.makeKing();   // make the piece a king if it reached the end of the board
        } else if (newY == 7 && piece.getType() == PieceType.BLACK) {
            nbBlackKings++;
            piece.makeKing();   // make the piece a king if it reached the end of the board
        }


        switch (result.getType()) {
            case NONE:
                piece.abortMove();
                return false;
            case NORMAL:
                piece.move(newX, newY);
                board[oldX][oldY].setPiece(null);
                board[newX][newY].setPiece(piece);
                break;
            case KILL:
                piece.move(newX, newY);
                board[oldX][oldY].setPiece(null);
                board[newX][newY].setPiece(piece);
                waitSecondKill = false;

                Piece otherPiece = result.getPiece();
                board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                pieceGroup.getChildren().remove(otherPiece);

                if (otherPiece.getType() == PieceType.WHITE) {
                    nbWhitePieces--;
                    if (otherPiece.getType().isKing) nbWhiteKings--;
                } else {
                    nbBlackPieces--;
                    if (otherPiece.getType().isKing) nbBlackKings--;
                }
                System.out.println("Now " + nbWhitePieces + " white pieces");
                System.out.println("Now " + nbBlackPieces + " black pieces");


                if ( !isOffline && (tryMove(newX, newY, newX + 2, newY + 2).getType() == MoveType.KILL
                        || tryMove(newX, newY, newX - 2, newY - 2).getType() == MoveType.KILL
                        || tryMove(newX, newY, newX + 2, newY - 2).getType() == MoveType.KILL
                        || tryMove(newX, newY, newX - 2, newY + 2).getType() == MoveType.KILL)) {
                    PieceX = newX;
                    PieceY = newY;
                    System.out.println("Wait for next kill");
                    statusLabel.setText("Your turn to move, combination");
                    waitSecondKill = true;
                    return false;
                }

                break;
        }


        return true;
    }


    public static void main(String[] args) {


        isOffline = Boolean.valueOf(args[0]);

        try {
            sender_id = Integer.parseInt(args[1]);
            receiver_id = Integer.parseInt(args[2]);
        }catch(Exception e){
            e.printStackTrace();
        }

        launch(args);

    }


    public class Tile extends Rectangle {

        private Piece piece;

        public boolean hasPiece() {
            return piece != null;
        }

        public Piece getPiece() {
            return piece;
        }

        public void setPiece(Piece piece) {
            this.piece = piece;
        }

        public Tile(boolean light, int x, int y) {

            setWidth(Checkers.TILE_SIZE);
            setHeight(Checkers.TILE_SIZE);

            relocate(x * Checkers.TILE_SIZE, y * Checkers.TILE_SIZE);

            setFill(light ? Color.valueOf("#feb") : Color.valueOf("#7a5722"));

        }
    }


    public enum MoveType {
        NONE, NORMAL, KILL
    }

    public class MoveResult {

        public MoveResult(MoveType type) {
            this(type, null);
        }

        public MoveResult(MoveType type, Piece piece) {
            this.type = type;
            this.piece = piece;
        }

        private MoveType type;
        private Piece piece;

        public MoveType getType() {
            return type;
        }

        public void setType(MoveType type) {
            this.type = type;
        }

        public Piece getPiece() {
            return piece;
        }
    }


}

