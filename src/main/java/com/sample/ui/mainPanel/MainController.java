package com.sample.ui.mainPanel;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import com.sample.chat.ChatQuery;
import com.sample.ocr.TessOcr;
import com.sample.ocr.User32Extra;
import com.sample.ui.filterPanel.FilterController;
import com.sample.ui.mainPanel.snipTool.SnipIt;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class MainController {


    private static Boolean finish = false;

    @FXML
    public ListView<String> buyingArea;

    private static String windowName;
    @FXML
    public ListView<String> sellingArea;

    @FXML
    JFXButton chatConfiguration;

    @FXML
    JFXButton filterChatButton;

    @FXML
    public JFXTextField nameField;

    @FXML
    StackPane stackPane;

    @FXML
    JFXToggleButton scannerOn;



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
//            Thread.currentThread().interrupt(); // Thread[Thread-5,5,main]
            System.out.println(Thread.currentThread() + "interrupted");
            return null;
        }
    };


    public static String getWindowName() {
        return windowName;
    }

    public void initialize() {
        scannerOn.setText(null);
        initListCellButtons();
    }


    private void initListCellButtons() {
        buyingArea.setCellFactory(entry -> new ListViewCellController() {
            {
                JFXSnackbar snackbar = new JFXSnackbar(stackPane);
                buyIcon.setOnMouseClicked(event -> {
//                        snackbar.setPrefWidth(250);
                    String selectedString = getListView().getSelectionModel().getSelectedItem();
                    String playerName = StringUtils.substringBefore(selectedString, "is selling");
                    String item = StringUtils.substringAfter(selectedString, "is selling:");
                    String clipboardString = "/w " + playerName + " how much for " + item + " ?";
                    StringSelection selection = new StringSelection(clipboardString);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(clipboardString.concat("\n") + "-> Copied to Clipboard"));
                    clipboard.setContents(selection, selection);
                });
            }

        });
        sellingArea.setCellFactory(entry -> new ListViewCellController() {
            {
                JFXSnackbar snackbar = new JFXSnackbar(stackPane);
                buyIcon.setOnMouseClicked(event -> {
//                        snackbar.setPrefWidth(200);
                    String selectedString = getListView().getSelectionModel().getSelectedItem();
                    String playerName = StringUtils.substringBefore(selectedString, "is buying");
                    String item = StringUtils.substringAfter(selectedString, "is buying:");
                    String clipboardString = "/w " + playerName + " how much for " + item + " ?";
                    StringSelection selection = new StringSelection(clipboardString);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(clipboardString.concat("\n") + "-> Copied to Clipboard"));
                    clipboard.setContents(selection, selection);
                });

            }
        });
    }

    private static void endThreads() {
        ChatQuery.setDone();
        TessOcr.getInstance().setDone();
//        setFinish(); // updateUIThread(ListView) does not restart. Therefor I'll not stop it. // Todo fix restart of updateUIThread
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
            endThreads();
        }
    }

    @FXML
    private void loadNoWndFound() {
        stackPane.mouseTransparentProperty().setValue(false);
        JFXDialogLayout content = new JFXDialogLayout();
        Label headerText = new Label("Window not found");
        headerText.setTextFill(Color.WHITE);
        content.setHeading(headerText);
        Label bodyText = new Label("The window \"" + windowName + "\" could not be detected. \n " +
                "Make sure to spell your character name right.");
        bodyText.setTextFill(Color.WHITE);
        Font font = new Font("sans-serif", 11.5);
        bodyText.setFont(font);
        content.setBody(bodyText);
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
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
        endThreads();
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
                    if (!line.trim().equals("")) {
                        buyingArea.getItems().add(line.trim());

                    }
                }
            }
            String line2;
            while ((line2 = br2.readLine()) != null) {
                if (!sellingArea.getItems().contains(line2.trim())) {
                    if (!line2.trim().equals("")) {
                        sellingArea.getItems().add(line2.trim());

                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Reading from file failed" + e.getMessage());
        }
    }

    @FXML
    public void handleChatConfiguration() {
        WinDef.HWND hWnd;
        validator.setMessage("Enter a name");
        nameField.getValidators().add(validator);
        if (!nameField.getText().trim().equals("") && nameField != null) {
            String windowToFind = nameField.getText().trim() + " - Dofus 2.47.16:1";
            hWnd = User32Extra.INSTANCE.FindWindow(null, windowToFind);
            if (hWnd == null) {
                loadNoWndFound();
            } else {
                windowName = windowToFind;
                User32.INSTANCE.SetForegroundWindow(hWnd);
                User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_SHOWMAXIMIZED);
                System.out.println("In main controller: " + windowName);
                new SnipIt();
                scannerOn.setDisable(false);
                scannerOn.requestFocus();
            }
        } else if (nameField.getText().trim().equals("")) {
            nameField.validate();
        }
    }

}
