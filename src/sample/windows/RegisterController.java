package sample.windows;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;

public class RegisterController {

    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXPasswordField confirmationlField;
    @FXML
    private JFXButton submitButton;


    public void initialize() {

        RequiredFieldValidator validator = new RequiredFieldValidator();
        nameField.getValidators().add(validator);
        emailField.getValidators().add(validator);
        passwordField.getValidators().add(validator);
        confirmationlField.getValidators().add(validator);
        validator.setMessage("Input Required");
        nameField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) nameField.validate();
        });
        passwordField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) passwordField.validate();
        });
        confirmationlField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) confirmationlField.validate();
        });
        emailField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) emailField.validate();
        });




    }
}
