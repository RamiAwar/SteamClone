package pumpkinbox.games.TicTacToe;

import pumpkinbox.api.GameStatus;
import pumpkinbox.client.GameClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TicTacToeClient {

    /**
     * Game name to insert into database.
     */
    public String gameTitle = "tictactoe";

    /**
     * Receiver id to be passed from controller.
     */
    public int receiver_id = 2;
    /**
     * Sender id to be passed from controller.
     */
    public int sender_id = 1;


    public String receiver_username = "UNKNOWN";

    public int gameStatus;

    private boolean initiator;

    /**
     * Setter for the game title so calling controller can modify it if needed
     * @param gameTitle
     */
    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    /**
     * Setter for the receiver_id so calling controller can set it.
     * @param receiver_id
     */
    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    /**
     * Setter for sender_id so calling controller can set it
     * @param sender_id
     */
    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    /**
     * Setter for receiver_username for forgotten reasons unfortunately.
     * @param receiver_username
     */
    public void setReceiver_username(String receiver_username) {
        this.receiver_username = receiver_username;
    }

    /**
     * Function to get whether or not this player started the game.
     * @return
     */
    public boolean isInitiator() {
        return initiator;
    }

    /**
     *Setter to set whether or not player it the game initiator.
     * @param initiator
     */
    public void setInitiator(boolean initiator) {
        this.initiator = initiator;
    }




    public JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel messageLabel = new JLabel("");

    private Square[] board = new Square[9];
    private Square currentSquare;
    private BufferedImage icon, opponentIcon, endIcon, winIcon, loseIcon, tieIcon;
    private boolean finishedGame = false;
    private boolean isOffline;
    private int roundNb = 0, l;

    private boolean isCurrentPlayer;    // variable that tells us if the which player's move we are waiting for

    String response, gameState;


    // Constructs the client by connecting to a server, laying out the GUI and registering GUI listeners.

    public TicTacToeClient(boolean isStartingPlayer, boolean isOffline) throws Exception {

        // Layout GUI
        this.isCurrentPlayer = isStartingPlayer;
        this.isOffline = isOffline;
        try {
            icon = ImageIO.read(new File("resources/images/games/TicTacToe/app-icon.png"));
            opponentIcon = ImageIO.read(new File("resources/images/games/TicTacToe/tic-tac-toe-O.png"));
            winIcon = ImageIO.read(new File("resources/images/games/TicTacToe/win.png"));
            loseIcon = ImageIO.read(new File("resources/images/games/TicTacToe/lose.png"));
            tieIcon = ImageIO.read(new File("resources/images/games/TicTacToe/win.png"));
            frame.setIconImage(ImageIO.read(new File("resources/images/games/TicTacToe/app-icon.png")));
        } catch (IOException ex) {
            System.out.println("Could not find images");
        }


        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, "South");

        JPanel boardPanel = new JPanel();
        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 3, 3));
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();

            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {

                    if (!isCurrentPlayer) {
                        messageLabel.setText("Not your turn, waiting for opponent to move");
                        return;
                    }
                    currentSquare = board[j];
                    //Checking that place clicked is non empty
                    if (currentSquare.seed != Seed.EMPTY) {
                        messageLabel.setText("Invalid move");
                        return;
                    }

                    //Printing move to the screen
                    currentSquare.setCellImage(icon);   // display the move on the screen
                    currentSquare.seed = Seed.CROSS;    // save the move on the board
                    messageLabel.setText("Waiting for opponent to move");
                    currentSquare.repaint();            // repaint the changed square
                    sendMove(j);

                    isCurrentPlayer = false;
                    if (checkEndGame())
                        finishedGame = true;

                }
            });
            boardPanel.add(board[i]);
        }
        frame.getContentPane().add(boardPanel, "Center");
    }


    public void play() throws Exception {

        char mark = 'X';
        frame.setTitle("Tic Tac Toe - Player " + mark);


        while (!finishedGame) {
            if (!isCurrentPlayer) {
                messageLabel.setText("Waiting for opponent to move");
                int loc = Integer.parseInt(receiveMove());
                board[loc].seed = Seed.NOUGHT;
                board[loc].setCellImage(opponentIcon);
                board[loc].repaint();
                isCurrentPlayer = true;
                if (checkEndGame())
                    finishedGame = true;
            }
            messageLabel.setText("Your turn to move");

            try {
                TimeUnit.MILLISECONDS.sleep(110);   // short delay to make the game
            } catch (InterruptedException e1) {             // less demanding on the cpu
                e1.printStackTrace();
            }
        }

    }


    public boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame,
                "Want to play again?", gameState,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(endIcon));
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }

    // winner
    public boolean didWin(int seed) {
        return
                (board[0].seed == seed && board[0].seed == board[1].seed && board[0].seed == board[2].seed)
                        ||(board[3].seed == seed && board[3].seed == board[4].seed && board[3].seed == board[5].seed)
                        ||(board[6].seed == seed && board[6].seed == board[7].seed && board[6].seed == board[8].seed)
                        ||(board[0].seed == seed && board[0].seed == board[3].seed && board[0].seed == board[6].seed)
                        ||(board[1].seed == seed && board[1].seed == board[4].seed && board[1].seed == board[7].seed)
                        ||(board[2].seed == seed && board[2].seed == board[5].seed && board[2].seed == board[8].seed)
                        ||(board[0].seed == seed && board[0].seed == board[4].seed && board[0].seed == board[8].seed)
                        ||(board[2].seed == seed && board[2].seed == board[4].seed && board[2].seed == board[6].seed);
    }


    int checkCombination(int seed) {
        int empty = Seed.EMPTY;
        // horizontal
        for (int i = 0; i < 3; i++) {
            if (board[3*i].seed == seed && board[3*i+1].seed == seed && board[3*i+2].seed == empty) return 3*i+2;
            if (board[3*i].seed == seed && board[3*i+1].seed == empty && board[3*i+2].seed == seed) return 3*i+1;
            if (board[3*i].seed == empty && board[3*i+1].seed == seed && board[3*i+2].seed == seed) return 3*i;
        }
        // vertical
        for (int i = 0; i < 3; i++) {
            if (board[i].seed == seed && board[3+i].seed == seed && board[6+i].seed == empty) return 6+i;
            if (board[i].seed == seed && board[3+i].seed == empty && board[6+i].seed == seed) return 3+i;
            if (board[i].seed == empty && board[3+i].seed == seed && board[6+i].seed == seed) return i;
        }
        // diagonals
        if (board[0].seed == seed && board[4].seed == seed && board[8].seed == empty) return 8;
        if (board[0].seed == seed && board[4].seed == empty && board[8].seed == seed) return 4;
        if (board[0].seed == empty && board[4].seed == seed && board[8].seed == seed) return 0;
        if (board[2].seed == seed && board[4].seed == seed && board[6].seed == empty) return 6;
        if (board[2].seed == seed && board[4].seed == empty && board[6].seed == seed) return 4;
        if (board[2].seed == empty && board[4].seed == seed && board[6].seed == seed) return 2;

        return -1;  // if no combination was found, return -1
    }

    private String AIOpponent() {
        Random rand = new Random();

        if (roundNb == 1) {     // opponent's move is the first
            int []possMoves = {0, 2, 4, 6, 8};
            do l = possMoves[rand.nextInt(5)];     // pick between the center and the corners
            while (board[l].seed != Seed.EMPTY);

            return(Integer.toString(l));
        }

        if (roundNb == 2)   {   // opponent's first move after player
            if (board[4].seed == Seed.CROSS) {              // if the player's first move is the center
                int []possMoves = {0, 2, 6, 8};
                l = possMoves[rand.nextInt(4)];     // pick a corner
            }

            else if (board[0].seed == Seed.CROSS || board[2].seed == Seed.CROSS || board[6].seed == Seed.CROSS || board[8].seed == Seed.CROSS)
                l = 4;  // if the player's first move is a corner, pick the center

            else {      // pick between the center and the corners
                int []possMoves = {0, 2, 4, 6, 8};
                do l = possMoves[rand.nextInt(5)];
                while (board[l].seed != Seed.EMPTY);
            }
            return(Integer.toString(l));
        }

        if (checkCombination(Seed.NOUGHT) != -1)          // check if there is a winning position
            l = checkCombination(Seed.NOUGHT);            // pick it

        else if (checkCombination(Seed.CROSS) != -1)      // else if the opp has a winning position
            l = checkCombination(Seed.CROSS);             // pick it

        else {                                            // else pick random position
            int []possMoves = {0, 2, 4, 6, 8};            // between the center and the corners
            do l = rand.nextInt(9);                //
            while (board[l].seed != Seed.EMPTY);
        }

        return Integer.toString(l);
    }

    // no empty squares
    public boolean boardFilledUp() {
        for (int i = 0; i < 9; i++) {
            if (board[i].seed == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean checkEndGame() {

        if (didWin(Seed.CROSS)){
            messageLabel.setText("You win");
            gameState = "You win!";
            gameStatus = GameStatus.WIN;
            endIcon = winIcon;
        } else if (didWin(Seed.NOUGHT)) {
            messageLabel.setText("You lose");
            gameStatus = GameStatus.LOSS;
            gameState = "You lose!";
            endIcon = loseIcon;
        } else if (boardFilledUp()) {
            messageLabel.setText("You tied");
            gameState = "It's a tie!";
            endIcon = tieIcon;
            gameStatus = GameStatus.TIE;
        } else{
            return false;
        }

        if(initiator) {
            GameClient.sendGameStatus(sender_id, receiver_id, receiver_username, gameTitle, gameStatus);
        }
        return true;
    }


    public void sendMove(Integer location) {

        roundNb++;
        if (isOffline)
            return;

        //send move
        GameClient.sendGameMove(sender_id, receiver_id, "tictactoe", Integer.toString(location));

    }

    public String receiveMove() {

        roundNb++;
        if (isOffline)
            return AIOpponent();

        //receive move
        return GameClient.receiveGameMove(sender_id, receiver_id, "tictactoe");

    }


    //main
    public static void main(String[] args) throws Exception {
        while (true) {
            TicTacToeClient client = new TicTacToeClient(true, true);
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setSize(500, 500);
            client.frame.setVisible(true);
            client.frame.setResizable(false);
            client.play();
            if (!client.wantsToPlayAgain()) {
                break;
            }
        }
    }


    //Graphical square in the client window.
    static class Square extends JPanel {
        JLabel label = new JLabel((Icon) null);
        int seed;

        public void setLabel(JLabel label) {
            this.label = label;
        }

        private BufferedImage cellImage;

        public Square() {
            setBackground(Color.white);
            add(label);
            seed = Seed.EMPTY;
        }

        public void setCellImage(BufferedImage cellImage) {
            this.cellImage = cellImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //this.getWidth()-cellImage.getWidth()/2
            g.drawImage(cellImage, 27, 27, this);
        }

    }

    public static class Seed {
        public static int EMPTY = 0;
        public static int CROSS = 1;
        public static int NOUGHT = -1;
    }

}