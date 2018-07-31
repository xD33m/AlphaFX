package sample.login;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import sample.Controller;

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


//        if (username.equals(preference.getUsername()) && password.equals(preference.getPassword())) {
        if (username.equals("admin") && password.equals("admin")) { // TODO verify login input w/ database
            closeStage();
            //TODO load main application
        } else {
            nameField.getStyleClass().add("wrong-credentials");
            passwordField.getStyleClass().add("wrong-credentials");
        }
    }

    private void closeStage() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    private void loadRegister() {
        new Controller().loadWindow("sample/register/register.fxml", "Register");
    }


}
