package com.sample;

import com.jfoenix.controls.JFXDecorator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Controller {
    public static double xOffset = 0;
    public static double yOffset = 0;

    public void loadWindow(String loc, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getClassLoader().getResource(loc));
            Stage stage = new Stage();
            JFXDecorator decorator = new JFXDecorator(stage, parent, false, false, true);
            decorator.setCustomMaximize(true);
            String uri = getClass().getClassLoader().getResource("css/dark.theme.css").toExternalForm();
            Scene scene = new Scene(decorator);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            scene.getStylesheets().add(uri);

            scene.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            scene.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });


            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
