package com.sample.ui.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class notificationServiceController {

    @FXML
    JFXButton userTokenSubmit;
    @FXML
    JFXButton testButton;
    @FXML
    private JFXTextField userTokenArea;

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

    @FXML
    private void onSubmit() {
//        DataSource.getInstance().insertToken(userToken, "7"); // TODO set token for logged in user.
    }


}
