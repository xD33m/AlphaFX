package sample;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;

public class Controller {

    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXPasswordField passwordField;


    public void initialize(){
        passwordField.setLabelFloat(true);
        emailField.setPromptText("Enter an Email");
        nameField.setPromptText("Enter a Nickname");
        passwordField.setPromptText("Enter a Password");

        RequiredFieldValidator validator = new RequiredFieldValidator();
        nameField.getValidators().add(validator);
        validator.setMessage("Input Required");
        nameField.focusedProperty().addListener((o,oldVal,newVal)-> {
            if (!newVal) nameField.validate();
        });
    }
}
