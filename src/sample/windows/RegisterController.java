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
    private JFXPasswordField confirmationField;
    @FXML
    private JFXButton submitButton;
//    @FXML
//    private StackPane root;

    @FXML
    public void initialize() {

        RequiredFieldValidator validator = new RequiredFieldValidator();
        nameField.getValidators().add(validator);
        emailField.getValidators().add(validator);
        passwordField.getValidators().add(validator);
        confirmationField.getValidators().add(validator);
        validator.setMessage("Input Required");
        nameField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) nameField.validate();
        });
        emailField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) emailField.validate();
        });
        passwordField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) passwordField.validate();
        });
        confirmationField.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) confirmationField.validate();
        });


    }
}
