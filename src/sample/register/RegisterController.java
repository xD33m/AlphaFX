package sample.register;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.ValidationFacade;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.Controller;
import sample.db.DataSource;

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
    @FXML
    private Text confirmationErrorText;

    @FXML
    public void initialize() {

    }

    @FXML
    private void loadLogin() {
        new Controller().loadWindow("sample/login/login.fxml", "Login");
    }

    @FXML
    private void handleSubmitButtonAction() {
        String userName = nameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String confirmation = confirmationField.getText();

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
            if(password.equals(confirmation)){
                confirmationErrorText.setVisible(false);
                DataSource.getInstance().insertUsers(userName, email, password);
                closeStage();
            }else {
                System.out.println("pwds don't match");
                confirmationErrorText.setVisible(true);
            }
        }

    }

    @FXML
    private void backToLogin(){
        loadLogin();
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

}
