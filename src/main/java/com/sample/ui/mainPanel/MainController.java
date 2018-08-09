package com.sample.ui.mainPanel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.sample.Controller;
import com.sample.chat.ChatQuery;
import com.sample.ocr.TessOcr;
import com.sample.ui.mainPanel.snipTool.SnipIt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


public class MainController {

    @FXML
    public TextArea buyingArea;
    @FXML
    public TextArea sellingArea;
    @FXML
    JFXToggleButton scannerOn;

    @FXML
    JFXButton chatConfiguration;

    @FXML
    JFXButton ocrStart;

    @FXML
    JFXButton filterChatButton;

    private ActionListener updateTask = e -> updateChatArea();
    private Timer updateTimer = new Timer(5000, updateTask);


    private Boolean done = true;

    public void initialize() {


    }

    public void handleChatConfiguration(ActionEvent event) {
        new SnipIt();
        scannerOn.setDisable(false);
        scannerOn.requestFocus();

    }

    @FXML
    public void onFilterChatButton() {
        new Controller().loadWindow("fxml/chatfilter.fxml", "Chat Filter");
    }


    public void handleStartOcr() {
        System.out.println("button pressed");
    }


    public void handleScannerOn(ActionEvent actionEvent) {
        Thread ocrThread = new Thread(TessOcr.getInstance());
        Thread queryThread = new Thread(new ChatQuery());
        queryThread.setDaemon(true);
        ocrThread.setDaemon(true);
        if (scannerOn.isSelected()) {
            ocrThread.start();
            queryThread.start();
            updateTimer.setRepeats(true);
            updateTimer.start();
        } else if (!scannerOn.isSelected()) {
            ChatQuery.setDone();
            TessOcr.getInstance().setDone();
            updateTimer.stop();
        }

    }

    private void updateChatArea() {
        try (BufferedReader br = new BufferedReader(new FileReader("PlayerSells.txt"));
             BufferedReader br2 = new BufferedReader(new FileReader("PlayerBuys.txt"))) {
            String line;
            if (buyingArea.getText().trim().equals("")) {
                String updateLine;
                while ((updateLine = br.readLine()) != null) {
                    buyingArea.appendText(updateLine.trim() + "\n");

                }
            }
            if (sellingArea.getText().trim().equals("")) {
                String updateLine;
                while ((updateLine = br2.readLine()) != null) {
                    sellingArea.appendText(updateLine.trim() + "\n");
                }
            }
            while ((line = br.readLine()) != null) {
                System.out.println("second while running");
                String[] s = buyingArea.getText().split("\n");
                for (String d : s) {
                    if (Arrays.asList(s).contains(d)) {
                        break;
                    }
                    buyingArea.appendText(line.trim() + "\n");
                }
            }
            String line2;
            while ((line2 = br2.readLine()) != null) {
                String[] s = sellingArea.getText().split("\n");
                for (String d : s) {
                    if (Arrays.asList(s).contains(d)) {
                        break;
                    }
                    sellingArea.appendText(line2.trim() + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Reading from file failed" + e.getMessage());
        }
    }
}
