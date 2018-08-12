package com.sample.ui.mainPanel;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.sample.chat.ChatQuery;
import com.sample.ocr.TessOcr;
import com.sample.ui.filterPanel.FilterController;
import com.sample.ui.mainPanel.snipTool.SnipIt;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class MainController {

    private static Boolean finish = false;
    @FXML
    public ListView<String> buyingArea;

    @FXML
    JFXToggleButton scannerOn;

    @FXML
    JFXButton chatConfiguration;

    @FXML
    JFXButton filterChatButton;
    @FXML
    public ListView<String> sellingArea;
    private Task updateTask = new Task<>() {
        @Override
        public Void call() throws Exception {
            while (!finish) {
                Platform.runLater(() -> updateChatArea());
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            finish = false;
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread() + "interuppted");

            return null;
        }
    };

    private static ListCell<String> call(ListView<String> lv) {
        ListCell<String> cell = new ListCell<String>() {
            private Label label = new Label();

            {
                label.setWrapText(true);
                label.setTextFill(Color.WHITE);
                label.maxWidthProperty().bind(Bindings.createDoubleBinding(
                        () -> getWidth() - getPadding().getLeft() - getPadding().getRight() - 1,
                        widthProperty(), paddingProperty()));
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(label);
                }
            }
        };
        return cell;
    }

    public void handleChatConfiguration(ActionEvent event) {
        new SnipIt();
        scannerOn.setDisable(false);
        scannerOn.requestFocus();

    }

    @FXML
    public void onFilterChatButton() {
        new FilterController().loadFilterWindow();
    }

    public void initialize() {
        // wrapping listView text:
        buyingArea.setCellFactory(MainController::call);
        sellingArea.setCellFactory(MainController::call);
    }

    private static void setFinish() {
        finish = true;
    }

    private void updateChatArea() {
        try (BufferedReader br = new BufferedReader(new FileReader("PlayerSells.txt"));
             BufferedReader br2 = new BufferedReader(new FileReader("PlayerBuys.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!buyingArea.getItems().contains(line.trim())) {
                    buyingArea.getItems().add(line.trim());
                }
            }
            String line2;
            while ((line2 = br2.readLine()) != null) {
                if (!sellingArea.getItems().contains(line2.trim())) {
                    sellingArea.getItems().add(line2.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Reading from file failed" + e.getMessage());
        }
    }

    public void handleScannerOn(ActionEvent actionEvent) {
        Thread ocrThread = new Thread(TessOcr.getInstance());
        Thread queryThread = new Thread(new ChatQuery());
        Thread updateUIThread = new Thread(updateTask);
        queryThread.setDaemon(true);
        ocrThread.setDaemon(true);
        updateUIThread.setDaemon(true);
        if (scannerOn.isSelected()) {
            ocrThread.start();
            queryThread.start();
            updateUIThread.start();
//            updateTimer.setRepeats(true);
//            updateTimer.start();
        } else if (!scannerOn.isSelected()) {
            ChatQuery.setDone();
            TessOcr.getInstance().setDone();
            setFinish();

//            UpdateUI.setDone();
//            updateTimer.stop();
        }

    }


}
