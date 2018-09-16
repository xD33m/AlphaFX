package com.sample.ui.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.sample.db.DataSource;
import com.sample.ui.mainPanel.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class notificationServiceController {

    @FXML
    Label successLabel;
    @FXML
    JFXButton userTokenSubmit;
    @FXML
    JFXButton testButton;
    @FXML
    private JFXTextField userTokenArea;

    @FXML
    JFXToggleButton phoneNotification;
    @FXML
    Label notifStateLabel;
    @FXML
    JFXButton cancelButton;

    @FXML
    ImageView playStoreBadge;
    @FXML
    ImageView appStoreBadge;

    public void initialize() {
        playStoreBadge.setOnMouseClicked(click -> {
            try {
                Desktop.getDesktop().browse(new URI("https://play.google.com/store/apps/details?id=com.sasneutrino.pushfleet2"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        appStoreBadge.setOnMouseClicked(click -> {
            try {
                Desktop.getDesktop().browse(new URI("https://itunes.apple.com/us/app/pushfleet/id1292327924"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        try (BufferedReader br = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\DofusChat\\userToken"))) {
            String line;
            if ((line = br.readLine()) != null) {
                userTokenArea.setText(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onTestButton() throws UnsupportedEncodingException {
        String url = "https://pushfleet.com/api/v1/send";
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        final String appId = "AJ7HJVTE";
        String userToken = userTokenArea.getText();
        String message = "Test message";
        String msgUrl = "https://www.dofus.com/fr/mmorpg/communaute/annuaires/pages-persos/82190500201-yusai";

        String query = String.format("appid=%s&userid=%s&message=%s&url=%s",
                URLEncoder.encode(appId, charset),
                URLEncoder.encode(userToken, charset),
                URLEncoder.encode(message, charset),
                URLEncoder.encode(msgUrl, charset));

        if (!userToken.trim().equals("") && userToken.length() == 8) {
            try {
                URLConnection connection = new URL(url + "?" + query).openConnection();
                connection.setRequestProperty("Accept-Charset", charset);
                InputStream response = connection.getInputStream();
                successLabel.setVisible(false);
            } catch (IOException e) {
                successLabel.setText("Test failed");
                successLabel.setStyle("-fx-text-fill: red");
                successLabel.setVisible(true);
                e.printStackTrace();
            }

        }
        //      System.out.println(userToken); // UBTF6PC6
//      try (Scanner scanner = new Scanner(response)) {
//            String responseBody = scanner.useDelimiter("\\A").next();
//            System.out.println(responseBody);
//       }
    }

    private File createOrRetrieve(final String target) throws IOException {

        final Path path = Paths.get(target);

        if (Files.notExists(path)) {
            return Files.createFile(Files.createDirectories(path)).toFile();
        }
        return path.toFile();
    }

    @FXML
    private void onSubmit() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(createOrRetrieve(System.getenv("APPDATA") + "\\DofusChat\\userToken")))) {
            if (!(userTokenArea.getText().length() == 8)) {
                successLabel.setText("Token has to be 8 characters long!");
                successLabel.setStyle("-fx-text-fill: red");
                successLabel.setVisible(true);
            } else {
                if (DataSource.getInstance().insertToken(userTokenArea.getText(), FileUtils.readFileToString(new File(System.getenv("APPDATA") + "\\DofusChat\\session"), "UTF-8").trim())) {
                    bw.write(userTokenArea.getText());
                    MainController.notificationOn = phoneNotification.isSelected();

                    successLabel.setText("Settings saved");
                    successLabel.setStyle("-fx-text-fill: green");
                    successLabel.setVisible(true);
                } else {
                    successLabel.setText("Could not connect to the DB");
                    successLabel.setStyle("-fx-text-fill: red");
                    successLabel.setVisible(true);
                }
            }
        } catch (IOException e) {
            successLabel.setText("File not found");
            successLabel.setStyle("-fx-text-fill: red");
            successLabel.setVisible(true);
        }
    }

    @FXML
    private void onToggle() {
        if (phoneNotification.isSelected()) {
            notifStateLabel.setText("ON");
            notifStateLabel.setStyle("-fx-text-fill: green");
        } else {
            notifStateLabel.setText("OFF");
            notifStateLabel.setStyle("-fx-text-fill: red");
        }
    }

    @FXML
    private void onClose() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

}
