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
    JFXButton closeButton;

    @FXML
    ImageView playStoreBadge;
    @FXML
    ImageView appStoreBadge;

    private static final String textPath = System.getProperty("user.home") + "\\DofusChat\\txt\\";

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
    }

    @FXML
    private void onTestButton() throws Exception {
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
            URLConnection connection = new URL(url + "?" + query).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            InputStream response = connection.getInputStream();
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

    // TODO letztes mal hier: gerade handy notification feritg. jetzt versucht text files in appdata oder einer anderen, bessern location zu speichern
    // zZ bekomm ich noch ne exception: AccessDeniedException

    @FXML
    private void onSubmit() throws IOException {
        if (DataSource.getInstance().insertToken(userTokenArea.getText(), 7) && userTokenArea.getText().length() == 8) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(createOrRetrieve(textPath.concat("userToken.txt"))))) {
                bw.write(userTokenArea.getText());
            }
            successLabel.setText("Success!");
            successLabel.setStyle("-fx-text-fill: green");
            successLabel.setVisible(true);
        } else {
            successLabel.setText("Something went wrong");
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
        MainController.notificationOn = phoneNotification.isSelected();
        ((Stage) closeButton.getScene().getWindow()).close();

    }

}
