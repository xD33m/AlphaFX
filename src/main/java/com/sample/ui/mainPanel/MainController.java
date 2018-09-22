package com.sample.ui.mainPanel;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import com.sample.Controller;
import com.sample.chat.ChatQuery;
import com.sample.ocr.TessOcr;
import com.sample.ocr.imageProcessing.User32Extra;
import com.sample.ui.mainPanel.snipTool.SnipIt;
import com.sample.ui.tradeNotification.tray.animations.AnimationType;
import com.sample.ui.tradeNotification.tray.notification.TrayNotification;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static com.sample.Main.*;

public class MainController {


    @FXML
    public ListView<String> buyingArea;
    @FXML
    public ListView<String> sellingArea;
    @FXML
    JFXButton chatConfiguration;
    @FXML
    JFXButton filterChatButton;
    @FXML
    public JFXTextField nameField;
    @FXML
    JFXToggleButton scannerOn;
    @FXML
    StackPane stackPane;
    @FXML
    FontAwesomeIconView settingIcon;
    private static final String mainWindowName = "Main";
    private static Boolean finish = false;
    private static String windowName;
    @FXML
    AnchorPane mainAnchorPane;
    private RequiredFieldValidator validator = new RequiredFieldValidator();
    public static boolean notificationOn;
    private Task updateTask = new Task() {
        @Override
        public Void call() {
            while (!finish) {
                Platform.runLater(() -> {
                    try {
                        updateChatArea();
                    } catch (IOException e) {
//                        System.out.println("notification error");
                        e.printStackTrace();
                    }
                });
                try {
                    Thread.sleep(400);
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
                    try {
                        deleteLine("PlayerSells.txt", selectedString);
                    } catch (IOException e) {
                        System.out.println("Could not delete line");
                    }
                    getListView().getItems().remove(selectedString);

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
                    try {
                        deleteLine("PlayerBuys.txt", selectedString);
                    } catch (IOException e) {
                        System.out.println("Could not delete line");
                    }
                    getListView().getItems().remove(selectedString);
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
    public void openSettings() {
        Controller controller = new Controller();
        Stage mainStage = (Stage) mainAnchorPane.getScene().getWindow();
        controller.loadChild("fxml/settings.fxml", mainStage);
    }

    @FXML
    public void onFilterChatButton() {
        new Controller().loadChild("fxml/chatfilter.fxml", (Stage) mainAnchorPane.getScene().getWindow());
    }

    private static void setFinish() {
        finish = true;
    }

    @FXML
    private void updateChatArea() throws IOException {
        File playersellstxt = new File(PlayerSellsTXT);
        File playerbuystxt = new File(PlayerBuysTXT);
        if (!playerbuystxt.isFile() && !playerbuystxt.createNewFile() && !playerbuystxt.exists()) {
            throw new IOException("Error creating new file: " + playerbuystxt.getAbsolutePath());
        } else if (!playersellstxt.isFile() && playersellstxt.createNewFile() && !playersellstxt.exists()) {
            throw new IOException("Error creating new file: " + playersellstxt.getAbsolutePath());
        }
        try (BufferedReader br = new BufferedReader(new FileReader(PlayerSellsTXT));
             BufferedReader br2 = new BufferedReader(new FileReader(PlayerBuysTXT))) {
            addItemToListView(br, buyingArea);
            addItemToListView(br2, sellingArea);
        } catch (IOException e) {
            System.out.println("Reading from file failed. " + e.getMessage());
        }
    }

    private void addItemToListView(BufferedReader br, ListView<String> listView) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (!listView.getItems().contains(line.trim())) {
                if (!line.trim().equals("")) {
                    listView.getItems().add(line.trim());
                    // windows notification
                    displayPopupMessage(line, listView);
                    // phone notification
                    if (notificationOn) {
                        sendPhoneNotification(line);
                    }

                }
            }
        }
    }

    private void sendPhoneNotification(String message) throws IOException {
        String url = "https://pushfleet.com/api/v1/send";
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();
        final String appId = "AJ7HJVTE";
        String userToken = org.apache.commons.io.FileUtils.readFileToString(new File(UserTokenPATH), "UTF-8").trim();
        String msgUrl = "https://www.dofus.com/fr/mmorpg/communaute/annuaires/pages-persos/82190500201-yusai";

        String query = String.format("appid=%s&userid=%s&message=%s&url=%s",
                URLEncoder.encode(appId, charset),
                URLEncoder.encode(userToken, charset),
                URLEncoder.encode(message, charset),
                URLEncoder.encode(msgUrl, charset));
        // if db query success ->
        URLConnection connection = new URL(url + "?" + query).openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        InputStream response = connection.getInputStream();
    }

    private void displayPopupMessage(String msg, ListView<String> listView) {
        Image icon = new Image("img/trayIcon.png");
        TrayNotification tray = new TrayNotification();
        tray.setTitle("New Trade Message");
        tray.setMessage(msg);
        if (listView.getId().equals("buyingArea")) { // Another Player sells:
            tray.setRectangleFill(Color.GREEN);

        } else {
            tray.setRectangleFill(Color.BROWN);
        }
        tray.setImage(icon);
        tray.setAnimationType(AnimationType.POPUP);
        tray.showAndDismiss(Duration.millis(10000));
    }

    @FXML
    public void handleChatConfiguration() {
        WinDef.HWND hWnd;
        validator.setMessage("Enter a name");
        nameField.getValidators().add(validator);
        if (!nameField.getText().trim().equals("") && nameField != null) {
            String windowToFind = nameField.getText().trim() + " - Dofus 2.48.8.0";
            WinDef.HWND mainhWnd = User32Extra.INSTANCE.FindWindow(null, mainWindowName);
            hWnd = User32Extra.INSTANCE.FindWindow(null, windowToFind);
            if (hWnd == null) {
                loadNoWndFound();
            } else {
                windowName = windowToFind;
                User32.INSTANCE.SetForegroundWindow(hWnd);
                User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_SHOWMAXIMIZED);
                User32.INSTANCE.SetForegroundWindow(mainhWnd);
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
