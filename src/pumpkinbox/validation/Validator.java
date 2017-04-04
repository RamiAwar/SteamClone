package pumpkinbox.validation;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.fontawesome.Icon;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.regex.Pattern;

/**
 * Created by ramiawar on 3/19/17.
 *
 *
 *
 * Created by ramiawar on 3/19/17.
 *
 *
 *
 * Created by ramiawar on 3/19/17.
 */
public class Validator {

    //DO NOT TOUCH. SIMPLE CHANGE RUINS VALIDATION.
    //*********************************************
    //                / \
    //               /   \
    //              /  |  \
    //             /   |   \
    //            /    o    \
    //           /_ _ _ _ _ _\
    //*********************************************
    public static final String EMAIL_VALIDATOR = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    public static final String MIN_LENGTH_VALIDATOR = "([\\S]{5}[\\S]+)";
    public static final String AGE_VALIDATOR = "([0-9][0-9])";
    public static final String DIGIT_VALIDATOR = "([0-9][0-9]+)";
    public static final String LETTERS_DIGITS_VALIDATOR = "([\\S]+[\\d])";
    public static final String SPACER = "                                                                             ";

    public static final String AGE = "age";
    public static final String NUMBER = "number";

    private Boolean VALID;

    private FadeTransition fadeIn = new FadeTransition(
            Duration.millis(400)
    );


    private Label label;
    private JFXTextField textField;
    private JFXPasswordField passwordField;

    RequiredFieldValidator textValidator = new RequiredFieldValidator();

    public void createEmailValidator(){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!isValidEmail(newValue)){
                if(!label.isVisible()){
                    label.setVisible(true);
                    fadeIn.playFromStart();}
            }
            else label.setVisible(false);
        });
    }

    public void createNumberValidator(String typename){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            if(typename.equals("age")) {
                if (!isValidAge(newValue)) {
                    if (!label.isVisible()) {
                        label.setVisible(true);
                        fadeIn.playFromStart();
                    }
                } else label.setVisible(false);
            }else if(typename.equals("number")){
                if (!isValidNumber(newValue)) {
                    if (!label.isVisible()) {
                        label.setVisible(true);
                        fadeIn.playFromStart();
                    }
                } else label.setVisible(false);
            }
        });
    }

    public void createPasswordValidator(){
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.isEmpty() || !isValidPassword(newValue)){
                if(!label.isVisible()){
                    label.setVisible(true);
                    fadeIn.playFromStart();}
            }
            else label.setVisible(false);
        });
    }

    public void createNonEmptyValidator(){
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            VALID = true;
            checkEmpty(newValue);
        });


    }


    //CONSTRUCTORS
    //TEXT FIELD VALIDATOR
    public Validator(Label label, JFXTextField textField, String message){

        this.label = label;
        this.textField = textField;

        setupAnimation(label);

        textField.getValidators().add(textValidator);
        updateLabel(message);

        Icon icon = new Icon(AwesomeIcon.WARNING,"1em",";","error");
        icon.setTextFill(Paint.valueOf("#eee"));
        label.setGraphic(icon);

        VALID = false;
    }

    //PASSWORD FIELD VALIDATOR
    public Validator(Label label, JFXPasswordField textField, String message){

        this.label = label;
        this.passwordField = textField;

        setupAnimation(label);

        passwordField.getValidators().add(textValidator);
        updateLabel(message);
        Icon icon = new Icon(AwesomeIcon.WARNING,"1em",";","error");
        icon.setTextFill(Paint.valueOf("#eee"));
        label.setGraphic(icon);

        VALID = false;

    }



    //VALIDATION REGEX CODE
    public boolean isValidEmail(String email){
        Pattern ptr = Pattern.compile(EMAIL_VALIDATOR);
        if(!ptr.matcher(email).matches()){
            updateLabel("Invalid email");
            VALID = false;
        }else
            VALID = true;
        return ptr.matcher(email).matches();
    }

    boolean isValidPassword(String password){

        //Check length minimum 6 chars
        Pattern ptr_length = Pattern.compile(MIN_LENGTH_VALIDATOR);
        if(!ptr_length.matcher(password).matches()) {
            updateLabel("At least 6 characters");
            VALID = false;
            return false;
        }

        //Check letters and digits
        Pattern ptr_strength = Pattern.compile(LETTERS_DIGITS_VALIDATOR);
        if(!ptr_strength.matcher(password).matches()){
            updateLabel("Include characters and digits");
            VALID = false;
            return false;
        }

        VALID = true;
        return true;
    }

    boolean isValidNumber(String text){
        Pattern ptr_length = Pattern.compile(DIGIT_VALIDATOR);
        if(!ptr_length.matcher(text).matches()) {
            updateLabel("Enter a valid number");
            VALID = false;
            return false;
        }
        VALID = true;
        return true;
    }

    boolean isValidAge(String text){
        Pattern ptr_length = Pattern.compile(AGE_VALIDATOR);
        if(!ptr_length.matcher(text).matches()) {

            if(text.getBytes().length < 2)
                updateLabel("You must be at least 10 years old");
            else
                updateLabel("Please enter your real age");

            VALID = false;
            return false;
        }
        VALID = true;
        return true;
    }

    private void checkEmpty(String newValue){
        if(newValue.isEmpty()){
            if(!label.isVisible()){
                label.setVisible(true);
                fadeIn.playFromStart();}
        }
        else label.setVisible(false);
    }

    public boolean validateNonempty(){
        if(textField.getText().isEmpty()){
            if(!label.isVisible()){
                label.setVisible(true);
                fadeIn.playFromStart();}
            return false;
        }
        else{
            label.setVisible(false);
            if(VALID) return true;
            else return false;
        }
    }

    private void setupAnimation(Node node){
        this.label.setVisible(false);
        fadeIn.setNode(node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);


    }

    private void updateLabel(String message){

        label.setText(message + SPACER);

    }

}
