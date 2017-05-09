package pumpkinbox.api;

/**
 * Created by ramiawar on 4/19/17.
 */
public class GameActivityObject {

    public GameActivityObject(String gameName, String experience, String status) {
        this.gameName = gameName;
        this.experience = experience;
        this.status = status;
        this.opponentName = "AI";
    }

    public GameActivityObject(String opponentName, String gameName, String experience, String status) {
        this.opponentName = opponentName;
        this.gameName = gameName;
        this.experience = experience;
        this.status = status;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    private String opponentName = "AI";
    private String gameName;
    private String experience;
    private String imageName;
    private String status;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
