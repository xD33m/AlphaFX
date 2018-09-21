package com.sample;

import com.jfoenix.controls.JFXDecorator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
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
            stage.getIcons().addAll(new Image(getClass().getClassLoader().getResourceAsStream("img/dofusIcon/dofus-icon-16.png")),
                    new Image(getClass().getClassLoader().getResourceAsStream("img/dofusIcon/dofus-icon-32.png")),
                    new Image(getClass().getClassLoader().getResourceAsStream("img/dofusIcon/dofus-icon-48.png")),
                    new Image(getClass().getClassLoader().getResourceAsStream("img/dofusIcon/dofus-icon-128.png")));
            stage.initStyle(StageStyle.UNDECORATED);
            JFXDecorator decorator = new JFXDecorator(stage, parent, false, false, true);
            decorator.setCustomMaximize(true);
            decorator.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("img/dofusIcon/dofus-icon-16.png"))));

            decorator.setTitle(title);
            String uri = getClass().getClassLoader().getResource("css/dark.theme.css").toExternalForm();
            Scene scene = new Scene(decorator);
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
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void loadChild(String loc, Stage owner) {
        try {
            Parent parent = FXMLLoader.load(getClass().getClassLoader().getResource(loc));
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            JFXDecorator decorator = new JFXDecorator(stage, parent, false, false, false);
            decorator.setCustomMaximize(true);
            String uri = getClass().getClassLoader().getResource("css/dark.theme.css").toExternalForm();
            Scene scene = new Scene(decorator);
            stage.setResizable(false);
            scene.getStylesheets().add(uri);
            stage.setScene(scene);
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
