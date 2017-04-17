package pumpkinbox.ui.images;

import javafx.scene.image.Image;

/**
 * Created by ramiawar on 4/8/17.
 */
public class Images {

    public static Image pumpkin = new Image("images/pumpkin.png");
    public static Image pumpkin_logo = new Image("images/pumpkin_profile.png");
    public static Image profile1 = new Image("images/pro1.png");
    public static Image profile2 = new Image("images/pro2.png");
    public static Image pumpkin_logo_gif = new Image("images/pumpkinold.gif");


    public static String tictactoe = "images/games/tictactoe.png";


    public static Image getImage(String img){
        switch (img){
            case "PRO1":
                return profile1;
            case "PRO2":
                return profile2;
            case "PUMPKIN":
                return pumpkin;
            default:
                return pumpkin;
        }
    }

}
