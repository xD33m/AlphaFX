package com.sample.mainPanel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXToggleButton;
import com.sample.mainPanel.snipTool.SnipIt;
import com.sample.ocr.TessOcr;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class mainController {

    @FXML
    JFXToggleButton scannerOn;
    @FXML
    JFXTextArea chatInfoArea;

    @FXML
    JFXButton chatConfiguration;

    @FXML
    JFXButton ocrStart;

    public void initialize() {

    }

    public void handleChatConfiguration(ActionEvent event) {
        new SnipIt();
        scannerOn.setDisable(false);

    }

    public void handleStartOcr() {
        System.out.println("button pressed");
    }


    public void handleScannerOn(ActionEvent actionEvent) {
        if (scannerOn.isSelected()) {
            TessOcr.getInstance().startOcr();
            chatInfoArea.appendText(TessOcr.getInstance().getOcrText()); // Todo getfilteredOcrText ?
        }
    }
}
