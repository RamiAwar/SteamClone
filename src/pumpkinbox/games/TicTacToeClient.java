package pumpkinbox.games;

import pumpkinbox.client.GameClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;

public class TicTacToeClient {

    private JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel messageLabel = new JLabel("");

    private Square[] board = new Square[9];
    private Square currentSquare;

    private BufferedImage icon;
    private BufferedImage opponentIcon;
    String response;

    // Constructs the client by connecting to a server, laying out the GUI and registering GUI listeners.

    public TicTacToeClient() throws Exception {

        // Layout GUI
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


                    currentSquare = board[j];
                    //Checking that place clicked is non empty
                    if (currentSquare.seed != Seed.EMPTY) {
                        messageLabel.setText("Invalid move");
                        return;
                    }

                    //Printing move to the screen
                    currentSquare.setCellImage(icon);
                    currentSquare.repaint();

                    //Notify user that were waiting for opponent
                    messageLabel.setText("Waiting for opponent's move...");


                    //Sending move to server and receiving responsen
                    response = GameClient.sendGameMove(2, 1, "XO", Integer.toString(j));

                    int loc = Integer.parseInt(response);
                    board[loc].setCellImage(opponentIcon);
                    board[loc].repaint();
                    messageLabel.setText("Opponent moved, your turn");

                    if (didWin(Seed.CROSS)){
                        messageLabel.setText("You win");
                        // TODO: end game
                    } else if (didWin(Seed.NOUGHT)) {
                        messageLabel.setText("You lose");
                    } else if (boardFilledUp()) {
                        messageLabel.setText("You tied");
                    }
                }
            });
            boardPanel.add(board[i]);
        }
        frame.getContentPane().add(boardPanel, "Center");
    }

    //* The main thread of the client will listen for messages from the server.
    //The first message will be a "WELCOME" message in which we receive our mark.
    //Then we go into a loop listening for:
    //--> "VALID_MOVE", --> "OPPONENT_MOVED", --> "VICTORY", --> "DEFEAT", --> "TIE", --> "OPPONENT_QUIT, --> "MESSAGE" messages, and handling each message appropriately.
    //The "VICTORY","DEFEAT" and "TIE" ask the user whether or not to play another game.
    //If the answer is no, the loop is exited and the server is sent a "QUIT" message.  If an OPPONENT_QUIT message is recevied then the loop will exit and the server will be sent a "QUIT" message also.
    public void play() throws Exception {
        char mark = 'X';
        try {
            icon = ImageIO.read(new File("resources/images/games/TicTacToe/tic-tac-toe-X.png"));
            opponentIcon = ImageIO.read(new File("resources/images/games/TicTacToe/tic-tac-toe-O.png"));
        } catch (IOException ex) {
            System.out.println("Could not find images");
        }

        frame.setTitle("Tic Tac Toe - Player " + mark);

        while (true) {

        }

    }


    private boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame,
                "Want to play again?",
                "Tic Tac Toe is Fun Fun Fun",
                JOptionPane.YES_NO_OPTION);
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

    // no empty squares
    public boolean boardFilledUp() {
        for (int i = 0; i < board.length; i++) {
            if (board[i].seed == Seed.EMPTY) {
                return false;
            }
        }
        return true;
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
//            this.getWidth()-cellImage.getWidth()/2
        g.drawImage(cellImage, 27, 27, this);
    }

}


    //main
    public static void main(String[] args) throws Exception {
        while (true) {
            TicTacToeClient client = new TicTacToeClient();
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
}