package pumpkinbox.games.checkers;

import java.util.ArrayList;

/**
 * Created by karlhayek on 5/3/17.
 */
public class AI {
    private Tree decisionTree;
    Board board;

    public AI(Checkers.Tile board[][], int nbWhitePieces, int nbBlackPieces, int nbWhiteKings, int nbBlackKings) {
        this.board = new Board(board, nbWhitePieces, nbBlackPieces, nbWhiteKings, nbBlackKings);
    }

    String getMove() {
//        board.print();
        decisionTree = makeDescisionTree(board);
        String move = pickMove().toString();
        System.out.println("Checkers.AI move: " + move);
        return move;
//        return ("1 2 2 3");
    }

    /**
     * Creates a tree with a height of four that has all possible moves
     * for the next three moves of the game
     *
     * @param board the board that the tree will be based on
     * @return a tree with all possible moves
     */
    private Tree makeDescisionTree(Board board) {
        Tree mainTree = new Tree(board, null, board.score(PieceType.BLACK));

//        board.print();
        ArrayList<Move> moves = board.getAllLegalMovesFor(PieceType.BLACK);
//        System.out.println("Length of move 1 :" + moves.size());

        for (Move move : moves) {
            // Make second row
            Board temp = new Board(board);
            temp.movePiece(move.currRow, move.currCol, move.movRow, move.movCol);

//            temp.print();
            Tree firstLayer = new Tree(temp, move, board.score(PieceType.WHITE));
            ArrayList<Move> secondMoves = temp.getAllLegalMovesFor(PieceType.WHITE);
//            System.out.println("Length of move 2 :" + secondMoves.size());

            for (Move sMove : secondMoves) {
                // Make third row
                Board temp2 = new Board(temp);
                temp2.movePiece(sMove.currRow, sMove.currCol, sMove.movRow, sMove.movCol);

                Tree secondLayer = new Tree(temp2, sMove, board.score(PieceType.BLACK));
                ArrayList<Move> thirdMoves = temp2.getAllLegalMovesFor(PieceType.BLACK);
//                System.out.println("Length of move 3 :" + thirdMoves.size());

                for (Move tMove : thirdMoves) {
                    // Make fourth row
                    Board temp3 = new Board(temp2);
                    temp3.movePiece(tMove.currRow, tMove.currCol, tMove.movRow, tMove.movCol);
//                    temp3.print();

                    secondLayer.addChild(new Tree(temp3, tMove, board.score(PieceType.WHITE)));
                }

                firstLayer.addChild(secondLayer);
            }
            mainTree.addChild(firstLayer);
        }

        return mainTree;
    }

    private Move pickMove() {
        int max = -13;
        int index = 0;
        for (int i = 0; i < decisionTree.getNumChildren(); i++) {
            Tree child = decisionTree.getChild(i);
            int smin = 13;
            // Find the max leaf
            for (Tree sChild : child.getChildren()) {
                int tMax = -13;
                for (Tree tchild : sChild.getChildren()) {
                    if (tchild.getScore() >= tMax) {
                        tMax = tchild.getScore();
                    }
                }
                sChild.setScore(tMax);
                // Find the min on the third level
                if (sChild.getScore() <= smin) {
                    smin = sChild.getScore();
                }
            }
            child.setScore(smin);
            // Find the max on the second layer and save the index
            if (child.getScore() >= max) {

                Move move = decisionTree.getChild(index).getMove();
                Piece piece = board.board[move.currRow][move.currCol];
//                if (move.movCol - move.currCol == piece.type.moveDir || move.movCol - move.currCol != piece.type.moveDir*2 || piece.type.isKing) {
                max = child.getScore();
                index = i;
//                }
            }
        }


        return decisionTree.getChild(index).getMove();
    }


}

class Board {
    int nbWhitePieces, nbBlackPieces, nbWhiteKings, nbBlackKings;
    Piece board[][] = new Piece[8][8];

    public Board(Checkers.Tile board[][], int nbWhitePieces, int nbBlackPieces, int nbWhiteKings, int nbBlackKings) {
        this.nbWhitePieces = nbWhitePieces;
        this.nbWhiteKings = nbWhiteKings;
        this.nbBlackPieces = nbBlackPieces;
        this.nbBlackKings = nbBlackKings;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = new Piece(board[i][j].getPiece());
                if (this.board[i][j] != null && this.board[i][j].type != null)
                    this.board[i][j].type.moveDir = board[i][j].getPiece().type.moveDir;
            }
    }

    public Board(Board boardToCopy) {   // copy constructor
        this.nbWhitePieces = boardToCopy.nbWhitePieces;
        this.nbWhiteKings = boardToCopy.nbWhiteKings;
        this.nbBlackPieces = boardToCopy.nbBlackPieces;
        this.nbBlackKings = boardToCopy.nbBlackKings;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = new Piece(boardToCopy.board[i][j]);
                if (this.board[i][j] != null && this.board[i][j].type != null)
                    this.board[i][j].type.moveDir = boardToCopy.board[i][j].type.moveDir;
            }
    }

    public void print() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null || board[i][j].getType() == null) System.out.print(" | ");
                else {
                    if (board[i][j].getType() == PieceType.WHITE) System.out.print("W ");
                    if (board[i][j].getType() == PieceType.BLACK) System.out.print("B ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public int score(PieceType pieceType) {
        if (pieceType == PieceType.WHITE) {
//            return (nbWhitePieces + 3 * nbWhiteKings) - (nbBlackPieces + 3 * nbBlackKings);
            return nbWhitePieces - nbWhiteKings + (3 * nbWhiteKings) - (nbBlackPieces - nbBlackKings + (3 * nbBlackKings));
        } else {
//            return (nbBlackPieces + 3 * nbBlackKings) - (nbWhitePieces + 3 * nbWhiteKings);
            return nbBlackPieces - nbBlackKings + (3 * nbBlackKings) - (nbWhitePieces - nbWhiteKings + (3 * nbWhiteKings));
        }
    }


    ArrayList<Move> getAllLegalMovesFor(PieceType pieceType) {
        ArrayList<Move> moves = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece == null || piece.getType() == null
                        || piece.getType() != pieceType) continue;

//                System.out.println("Tried for: " + i + " " + j);

                if (board[i][j].type == PieceType.BLACK || board[i][j].type.isKing) {
                    if (tryMove(i, j, i + 1, j + 1) == Checkers.MoveType.NORMAL)
                        moves.add(new Move(i, j, i + 1, j + 1));
                    if (tryMove(i, j, i - 1, j + 1) == Checkers.MoveType.NORMAL)
                        moves.add(new Move(i, j, i - 1, j + 1));
                    if (tryMove(i, j, i - 2, j + 2) == Checkers.MoveType.KILL)
                        moves.add(new Move(i, j, i - 2, j + 2));
                    if (tryMove(i, j, i + 2, j + 2) == Checkers.MoveType.KILL)
                        moves.add(new Move(i, j, i + 2, j + 2));
                }
                if (board[i][j].type == PieceType.WHITE || board[i][j].type.isKing) {
                    if (tryMove(i, j, i - 1, j - 1) == Checkers.MoveType.NORMAL)
                        moves.add(new Move(i, j, i - 1, j - 1));
                    if (tryMove(i, j, i + 1, j - 1) == Checkers.MoveType.NORMAL)
                        moves.add(new Move(i, j, i + 1, j - 1));
                    if (tryMove(i, j, i - 2, j - 2) == Checkers.MoveType.KILL)
                        moves.add(new Move(i, j, i - 2, j - 2));
                    if (tryMove(i, j, i + 2, j - 2) == Checkers.MoveType.KILL)
                        moves.add(new Move(i, j, i + 2, j - 2));
                }

                count++;
                if (count == 12) {
//                    System.out.println("s: " + moves.size());
                    return moves;
                }
            }
        }
        return moves;

    }


    public boolean movePiece(int oldX, int oldY, int newX, int newY) {
//        System.out.println("move from " + oldX + " " + oldY + " to " + newX + " " + newY);
        Piece piece = board[oldX][oldY];


        Checkers.MoveType result = tryMove(oldX, oldY, newX, newY);

        if (newY == 0 && piece.getType() == PieceType.WHITE) {
            nbWhiteKings++;
            piece.makeKing();   // make the piece a king if it reached the end of the board
        } else if (newY == 7 && piece.getType() == PieceType.BLACK) {
            nbBlackKings++;
            piece.makeKing();   // make the piece a king if it reached the end of the board
        }

        switch (result) {
            case NONE:
                piece.abortMove();
                return false;
            case NORMAL:
                piece.move(newX, newY);
                board[oldX][oldY] = null;
                board[newX][newY] = piece;
                break;
            case KILL:
                piece.move(newX, newY);

                board[oldX + (newX - oldX) / 2][oldY + (newY - oldY) / 2] = null;

                if (board[oldX][oldY].getType() == PieceType.WHITE) {
                    nbWhitePieces--;
                    if (board[oldX][oldY].getType().isKing) nbWhiteKings--;
                } else {
                    nbBlackPieces--;
                    if (board[oldX][oldY].getType().isKing) nbBlackKings--;
                }

                board[oldX][oldY] = null;
                board[newX][newY] = piece;

                break;
        }
        return true;
    }

    private Checkers.MoveType tryMove(int oldX, int oldY, int newX, int newY) {
        Piece piece = board[oldX][oldY];

        Checkers.MoveType result = Checkers.MoveType.NONE;

        if (newX > 7 || newX < 0 || newY > 7 || newY < 0)
            return Checkers.MoveType.NONE;

        else if (board[newX][newY] == null || board[newX][newY].getType() != null || (newX + newY) % 2 == 0)
            result = Checkers.MoveType.NONE;

        else if (Math.abs(newX - oldX) == 1 && (newY - oldY == piece.getType().moveDir || piece.type.isKing)) {
            // if the piece is a king it can move in both directions
            result = Checkers.MoveType.NORMAL;
        } else if (Math.abs(newX - oldX) == 2 && (newY - oldY == piece.getType().moveDir * 2 || piece.type.isKing)) {

            int x1 = oldX + (newX - oldX) / 2;
            int y1 = oldY + (newY - oldY) / 2;

            if (board[x1][y1] != null && board[x1][y1].getType() != null && board[x1][y1].getType() != piece.getType()) {
                result = Checkers.MoveType.KILL;
            }
        }
        return result;
    }
}