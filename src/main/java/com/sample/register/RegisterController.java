package com.sample.register;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.sample.Controller;
import com.sample.db.DataSource;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

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

    private String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }



    @FXML
    private void loadLogin() {
        new Controller().loadWindow("fxml/login.fxml", "Login");

    }

    @FXML
    private void handleSubmitButtonAction() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String userName = nameField.getText().toLowerCase(); // case insensitive
        String encryptedPassword = generateStrongPasswordHash(passwordField.getText());
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

//        Pattern emailPattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");

        if (!validator.getHasErrors() && nameField.getText() == null) {
            if (password.equals(confirmation)) { // verifies password match
                confirmationErrorText.setVisible(false);
                if (DataSource.getInstance().verifyUsername(userName)) { // verifies existing users
                    confirmationErrorText.setText("User already exists");
                    confirmationErrorText.setVisible(true);
                } else if (!EmailValidator.getInstance().isValid(email)) {
                    confirmationErrorText.setText("Please enter valid email");
                    confirmationErrorText.setVisible(true);
                } else if (password.length() < 4) {
                    confirmationErrorText.setText("Password must be at least 4 characters");
                    confirmationErrorText.setVisible(true);
                } else {
                    DataSource.getInstance().insertUsers(userName, email, encryptedPassword);
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
