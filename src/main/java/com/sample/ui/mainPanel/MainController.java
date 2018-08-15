package com.sample.ui.mainPanel;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import com.sample.chat.ChatQuery;
import com.sample.ocr.TessOcr;
import com.sample.ocr.User32Extra;
import com.sample.ui.filterPanel.FilterController;
import com.sample.ui.mainPanel.snipTool.SnipIt;
import com.sun.jna.platform.win32.WinDef;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

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
    private static String windowName;
    @FXML
    public JFXTextField nameField;
    @FXML
    StackPane stackPane;
    private RequiredFieldValidator validator = new RequiredFieldValidator();

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
            System.out.println(Thread.currentThread() + "interrupted");

            return null;
        }
    };

    public static String getWindowName() {
        return windowName;
    }

    public void initialize() {
        // wrapping listView text:
        buyingArea.setCellFactory(MainController::call);
        sellingArea.setCellFactory(MainController::call);
        scannerOn.setText(null);
    }

    @FXML
    public void handleChatConfiguration() {
        WinDef.HWND hWnd;
        validator.setMessage("Enter a name");
        nameField.getValidators().add(validator);
        if (!nameField.getText().trim().equals("") && nameField != null) {
            windowName = nameField.getText().trim() + " - Dofus 2.47.16:1";
            hWnd = User32Extra.INSTANCE.FindWindow(null, windowName);
            if (hWnd == null) {
                loadNoWndFound();
            } else {
                User32Extra.INSTANCE.SetForegroundWindow(hWnd);
                System.out.println("In main controller: " + windowName);
                new SnipIt();
                scannerOn.setDisable(false);
                scannerOn.requestFocus();
            }
        } else if (nameField.getText().trim().equals("")) {
            nameField.validate();
        }
    }

    public void handleScannerOn() {
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
        } else if (!scannerOn.isSelected()) {
            ChatQuery.setDone();
            TessOcr.getInstance().setDone();
            setFinish();
        }
    }

    @FXML
    private void loadNoWndFound() {
        stackPane.mouseTransparentProperty().setValue(false);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Window not found"));
        content.setBody(new Text("The window \"" + windowName + "\" could not be detected.\n" +
                "Make sure to spell your character name right."));
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.TOP);
        JFXButton button = new JFXButton("Okay");
        dialog.setOnDialogClosed(event -> {
            stackPane.mouseTransparentProperty().setValue(true);
            nameField.requestFocus();
        });
        button.setOnAction(event -> {
            stackPane.mouseTransparentProperty().setValue(true);
            dialog.close();
            nameField.requestFocus();
        });
        content.setActions(button);
        dialog.show();
        scannerOn.selectedProperty().setValue(false);
    }

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

    @FXML
    public void onFilterChatButton() {
        new FilterController().loadFilterWindow();
    }

    private static void setFinish() {
        finish = true;
    }

    @FXML
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
}
