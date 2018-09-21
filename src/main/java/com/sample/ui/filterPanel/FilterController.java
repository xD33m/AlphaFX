package com.sample.ui.filterPanel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXChipView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.*;


public class FilterController {

    @FXML
    JFXButton confirmButton;
    @FXML
    private JFXChipView<String> buyArea;
    @FXML
    private JFXChipView<String> sellArea;

    public void initialize() {
        try (BufferedReader br = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\DofusChat\\text\\wts.txt"));
             BufferedReader br2 = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\DofusChat\\text\\wtb.txt"))) {
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



    @FXML
    private void handleConfirmButton() throws IOException {
        ObservableList<String> sellList = sellArea.getChips();
        ObservableList<String> buyList = buyArea.getChips();
        try (BufferedWriter wts = new BufferedWriter(new FileWriter(System.getenv("APPDATA") + "\\DofusChat\\text\\wts.txt"));
             BufferedWriter wtb = new BufferedWriter(new FileWriter(System.getenv("APPDATA") + "\\DofusChat\\text\\wtb.txt"))) {
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
