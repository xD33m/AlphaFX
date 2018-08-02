package com.sample.login;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import com.sample.Controller;
import com.sample.db.DataSource;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoginController {

    @FXML
    private JFXTextField nameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXButton registerText;
    @FXML
    private JFXButton loginButton;

    //verify password hash
    private static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }
    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }


    @FXML
    private void handleRegisterTextAction() {
        closeStage();
        loadRegister();
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String username = nameField.getText().toLowerCase();
        String password = passwordField.getText();

        String hashedDBPassword = DataSource.getInstance().getQueryPassword(username);

        RequiredFieldValidator validator = new RequiredFieldValidator();
        nameField.getValidators().add(validator);
        passwordField.getValidators().add(validator);
        validator.setMessage("Input Required");
        nameField.validate();
        passwordField.validate();

        Boolean userExists = DataSource.getInstance().verifyUsername(username);
        Boolean passwordMatch = validatePassword(password, hashedDBPassword);

        System.out.println("User exists: " + userExists +", Password is valid: "+ passwordMatch);

        if(userExists && passwordMatch){
            System.out.println("User found!");
        }
    }

    private void closeStage() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    private void loadRegister() {
        new Controller().loadWindow("fxml/register.fxml", "Register");
    }


}
