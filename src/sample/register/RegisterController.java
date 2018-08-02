package sample.register;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.Controller;
import sample.db.DataSource;

import java.util.regex.Pattern;

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

        Pattern emailPattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

        if (!validator.getHasErrors()) {
            if (password.equals(confirmation)) { // verifies password match
                confirmationErrorText.setVisible(false);
                if (DataSource.getInstance().verifyUsername(userName)) { // verifies existing users
                    confirmationErrorText.setText("User already exists");
                    confirmationErrorText.setVisible(true);
                } else if (!emailPattern.matcher(email).matches()) {
                    confirmationErrorText.setText("Please enter valid email");
                    confirmationErrorText.setVisible(true);
                } else if (password.length() < 4) {
                    confirmationErrorText.setText("Password must be at least 4 characters");
                    confirmationErrorText.setVisible(true);
                } else {
                    DataSource.getInstance().insertUsers(userName, email, password);
                    System.out.println("User: " + userName + " registered.");
                    closeStage();
                    loadLogin();
                }
            }else {
                confirmationErrorText.setText("Passwords don't match");
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
