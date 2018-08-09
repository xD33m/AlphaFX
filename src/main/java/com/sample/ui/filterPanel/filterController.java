package com.sample.ui.filterPanel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXChipView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class filterController {

    @FXML
    JFXButton confirmButton;
    @FXML
    private JFXChipView<String> buyArea;
    @FXML
    private JFXChipView<String> sellArea;


    @FXML
    private void handleConfirmButton() throws IOException {
        ObservableList<String> sellList = sellArea.getChips();
        ObservableList<String> buyList = buyArea.getChips();
        try (BufferedWriter wts = new BufferedWriter(new FileWriter("wts.txt", true));
             BufferedWriter wtb = new BufferedWriter(new FileWriter("wtb.txt", true))) {
            for (String s : sellList) {
                wts.write(s + "\r\n");
            }
            for (String s : buyList) {
                wtb.write(s + "\r\n");
            }
        }
        close();
    }


    private void close() {
        ((Stage) confirmButton.getScene().getWindow()).close();
    }


}
