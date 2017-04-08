package pumpkinbox.ui.icons;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import de.jensd.fx.glyphs.materialicons.MaterialIconView;
import javafx.scene.paint.Material;
import javafx.scene.paint.Paint;


/**
 * Created by ramiawar on 3/24/17.
 */

public class Icons {

    public FontAwesomeIconView ADD_USER;
    public FontAwesomeIconView LIST_USERS;
    public FontAwesomeIconView ADD_BOOK;
    public FontAwesomeIconView LIST_BOOKS;
    public FontAwesomeIconView SETTINGS;
    public FontAwesomeIconView RENEW ;
    public FontAwesomeIconView ISSUE ;
    public FontAwesomeIconView RETURN;//or FLASH
    public FontAwesomeIconView CLOSE;
    public FontAwesomeIconView MINIMIZE;
    public MaterialDesignIconView CLOSE_m;
    public MaterialDesignIconView MINIMIZE_m;
    public MaterialDesignIconView MINIMIZE_small;

    public MaterialDesignIconView SUCCESS;
    public MaterialDesignIconView SEARCH;

    public MaterialDesignIconView ADD;

    public MaterialDesignIconView GHOST_GREEN;
    public MaterialDesignIconView GHOST_RED;
    public MaterialDesignIconView GHOST_ORANGE;


    public Icons(){

        ADD_USER = new FontAwesomeIconView(FontAwesomeIcon.USER_PLUS);
        LIST_USERS = new FontAwesomeIconView(FontAwesomeIcon.USERS);
        ADD_BOOK = new FontAwesomeIconView(FontAwesomeIcon.LEANPUB);
        LIST_BOOKS = new FontAwesomeIconView(FontAwesomeIcon.LIST);
        SETTINGS = new FontAwesomeIconView(FontAwesomeIcon.COG);
        RENEW = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
        ISSUE = new FontAwesomeIconView(FontAwesomeIcon.SEND);
        RETURN = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);//or FLASH
        CLOSE = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
        MINIMIZE = new FontAwesomeIconView(FontAwesomeIcon.ANGLE_DOWN);
        CLOSE_m = new MaterialDesignIconView(MaterialDesignIcon.CLOSE);
        MINIMIZE_m = new MaterialDesignIconView(MaterialDesignIcon.MENU_DOWN);
        SUCCESS = new MaterialDesignIconView(MaterialDesignIcon.CHECK_ALL);
        SEARCH = new MaterialDesignIconView(MaterialDesignIcon.MAGNIFY);
        MINIMIZE_small = new MaterialDesignIconView(MaterialDesignIcon.MENU_DOWN);
        ADD = new MaterialDesignIconView(MaterialDesignIcon.PLUS);
        GHOST_GREEN = new MaterialDesignIconView(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE);
        GHOST_RED = new MaterialDesignIconView(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE);
        GHOST_ORANGE = new MaterialDesignIconView(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE);


    }

    public void setSize(String size){

        ADD_USER.setSize(size);
        LIST_USERS.setSize(size);
        ADD_BOOK.setSize(size);
        LIST_BOOKS.setSize(size);
        SETTINGS.setSize(size);

        SUCCESS.setSize("5em");

        SEARCH.setSize("1.5em");

        RENEW.setSize("1em");
        ISSUE.setSize("1em");
        RETURN.setSize("1em");

        ADD.setSize("1.3em");
        CLOSE.setSize("1.3em");
        MINIMIZE.setSize("1.3em");

        CLOSE_m.setSize("1.5em");
        MINIMIZE_m.setSize("1.8em");
        MINIMIZE_small.setSize("1.5em");
        GHOST_GREEN.setSize("1em");
        GHOST_ORANGE.setSize("1em");
        GHOST_RED.setSize("1em");

        CLOSE.setFill(Paint.valueOf("#fff"));
        ADD.setFill(Paint.valueOf("#fff"));
        MINIMIZE.setFill(Paint.valueOf("#fff"));
        MINIMIZE_small.setFill(Paint.valueOf("#ddd"));
        MINIMIZE_m.setFill(Paint.valueOf("#fff"));
        CLOSE_m.setFill(Paint.valueOf("#fff"));

        SUCCESS.setFill(Paint.valueOf("#fff"));
        SEARCH.setFill(Paint.valueOf("#fff"));

        GHOST_RED.setFill(Paint.valueOf("#c55"));
        GHOST_GREEN.setFill(Paint.valueOf("#5c5"));
        GHOST_ORANGE.setFill(Paint.valueOf("#fa4"));



    }

}
