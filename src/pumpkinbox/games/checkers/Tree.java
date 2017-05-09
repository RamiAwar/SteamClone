package pumpkinbox.games.checkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by karlhayek on 5/3/17.
 */

public class Tree {
    private Board node;
    private Move move;
    private int score;
    private ArrayList<Tree> children;

    /**
     * Creates a new tree with node as the head, a move, a score, and children
     *
     * @param node     the head of the tree
     * @param move     the move associated with the tree
     * @param score    the score of the tree
     * @param children the children of the node
     */
    public Tree(Board node, Move move, int score, Tree... children) {
        this.node = node;
        this.children = new ArrayList<>(Arrays.asList(children));
        this.score = score;
        this.move = move;
    }

    /**
     * @return the Checkers.Board of the tree
     */
    public Board getBoard() {
        return node;
    }

    /**
     * @return the move of the tree
     */
    public Move getMove() {
        return move;
    }

    /**
     * @return the score of the tree
     */
    public int getScore() {
        return score;
    }

    /**
     * @return the tree's children
     */
    public List<Tree> getChildren() {
        return children;
    }

    /**
     * @return the number of children the tree has
     */
    public int getNumChildren() {
        return children.size();
    }

    /**
     * Changes the tree's score
     *
     * @param newVal the new score of the tree
     */
    public void setScore(int newVal) {
        score = newVal;
    }

    /**
     * The child at the given index
     *
     * @param index the chosen index
     * @return the child at the index
     */
    public Tree getChild(int index) {
        return children.get(index);
    }

    /**
     * Adds a child to the tree
     *
     * @param child the tree that will be added to the children
     */
    public void addChild(Tree child) {
        children.add(child);
    }

    /**
     * Adds multiple children to the tree
     *
     * @param children the trees that will be added to the children
     */
    public void addChildren(Tree... children) {
        for (Tree child : children) {
            addChild(child);
        }
    }

}

    class Move {
        public int currRow, currCol, movRow, movCol;

        /**
         * Creates a new move
         * @param currRow the current row of the piece
         * @param currCol the current column of the piece
         * @param movRow the row the piece will be moved to
         * @param movCol the column the piece will be moved to
         */
        public Move(int currRow, int currCol, int movRow, int movCol) {
            this.currRow = currRow;
            this.currCol = currCol;
            this.movRow = movRow;
            this.movCol = movCol;
        }

        /**
         * Creates a string representation of a move
         * @return a string version of a move
         */
        public String toString() {
            return "" + currRow + " " + currCol + " " + movRow + " " + movCol + "";
        }
    }

