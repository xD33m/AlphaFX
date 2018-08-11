package com.sample.ui.filterPanel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXDecorator;
import com.sample.Controller;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;


public class FilterController {

    @FXML
    JFXButton confirmButton;
    @FXML
    private JFXChipView<String> buyArea;
    @FXML
    private JFXChipView<String> sellArea;

    public void initialize() {
        try (BufferedReader br = new BufferedReader(new FileReader("wts.txt"));
             BufferedReader br2 = new BufferedReader(new FileReader("wtb.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.equals("")) {
                    sellArea.getChips().addAll(line);
                }
            }
            String line2;
            while ((line2 = br2.readLine()) != null) {
                if (!line2.equals("")) {
                    buyArea.getChips().addAll(line2);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed populating chip view" + e.getMessage());
        }
    }

    public void loadFilterWindow() {
        try {
            Parent parent = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/chatfilter.fxml"));
            Stage stage = new Stage();
            JFXDecorator decorator = new JFXDecorator(stage, parent, false, false, false);
            decorator.setCustomMaximize(true);
            String uri = getClass().getClassLoader().getResource("css/dark.theme.css").toExternalForm();
            Scene scene = new Scene(decorator);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            scene.getStylesheets().add(uri);

            scene.setOnMousePressed(event -> {
                Controller.xOffset = event.getSceneX();
                Controller.yOffset = event.getSceneY();
            });
            scene.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - Controller.xOffset);
                stage.setY(event.getScreenY() - Controller.yOffset);
            });
            stage.setTitle("Filter");
            stage.setScene(scene);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handleConfirmButton() throws IOException {
        ObservableList<String> sellList = sellArea.getChips();
        ObservableList<String> buyList = buyArea.getChips();
        try (BufferedWriter wts = new BufferedWriter(new FileWriter("wts.txt"));
             BufferedWriter wtb = new BufferedWriter(new FileWriter("wtb.txt"))) {
            for (String s : sellList) {
                wts.write("\r\n" + s);
            }
            for (String s : buyList) {
                wtb.write("\r\n" + s);
            }
        }
        close();
    }


    private void close() {
        ((Stage) confirmButton.getScene().getWindow()).close();
    }


}
