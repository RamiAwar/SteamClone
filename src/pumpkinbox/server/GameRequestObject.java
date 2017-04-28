package pumpkinbox.server;

/**
 * Created by ramiawar on 4/26/17.
 */
public class GameRequestObject {

    int sender_id;
    int receiver_id;
    String move;
    String game;

    public GameRequestObject(int sender_id, int receiver_id, String game, String move) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.game = game;
        this.move = move;
    }

    public GameRequestObject() {
        this.sender_id = -1;
        this.receiver_id = -1;
        this.game = "";
        this.move = "";
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String request) {
        this.move = move;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }
}
