package sample.login;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import sample.Controller;
import sample.db.DataSource;

public class LoginController {

    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXButton registerText;
    @FXML
    private JFXButton loginButton;


    @FXML
    private void handleRegisterTextAction() {
        closeStage();
        loadRegister();
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = nameField.getText();
        String password = passwordField.getText();

        RequiredFieldValidator validator = new RequiredFieldValidator();
        nameField.getValidators().add(validator);
        passwordField.getValidators().add(validator);
        validator.setMessage("Input Required");
        nameField.validate();
        passwordField.validate();

        Boolean exists = DataSource.getInstance().verifyUser(username, password);

        if(exists){
            System.out.println("User found!");
        }
    }

    private void closeStage() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    private void loadRegister() {
        new Controller().loadWindow("sample/register/register.fxml", "Register");
    }


}
