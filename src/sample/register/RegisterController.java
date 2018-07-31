package sample.register;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import sample.Controller;

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

    }

    @FXML
    private void loadLogin() {
        new Controller().loadWindow("sample/login/login.fxml", "Login");
    }

    @FXML
    private void handleSubmitButtonAction() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        nameField.getValidators().add(validator);
        emailField.getValidators().add(validator);
        passwordField.getValidators().add(validator);
        confirmationField.getValidators().add(validator);
        validator.setMessage("Input Required");
        nameField.validate();
        emailField.validate();
        passwordField.validate();
        confirmationField.validate();
        if (!validator.getHasErrors()) {            //TODO entry verification with database
            closeStage();
            loadLogin();
        }

    }

    private void closeStage() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

}
