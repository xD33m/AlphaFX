package com.sample.ui.mainPanel;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class ListViewCellController extends ListCell<String> {

    @FXML
    private Text text;

    @FXML
    private GridPane gridPane;

    @FXML
    private FXMLLoader fxmlLoader;

    @FXML
    private FontAwesomeIconView closeIcon;

    @FXML
    public FontAwesomeIconView buyIcon;

//    @FXML
//    JFXSnackbar snackbar;

    public ListViewCellController() {
        super();
        if (fxmlLoader == null) {
            fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/ListViewCell.fxml"));
            fxmlLoader.setController(this);
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeIcon.setOnMouseClicked(click -> {
            try {
                String lineToRemove = getListView().getSelectionModel().getSelectedItem();
                if (getListView().getId().equals("buyingArea")) {
                    deleteLine("PlayerSells.txt", lineToRemove);
                    getListView().getItems().remove(lineToRemove);
                } else {
                    deleteLine("PlayerBuys.txt", lineToRemove);
                    getListView().getItems().remove(lineToRemove);
                }

            } catch (IOException e) {
                System.out.println("Failed deleting line");
                e.printStackTrace();
            }
        });
    }

    private void addToBlacklist(String lineToRemove, BufferedWriter BLwriter, File blacklist) throws IOException {
        String lineToBlacklist = "\r\n" + lineToRemove;
        if (!FileUtils.readFileToString(blacklist, "UTF-8").contains(lineToBlacklist)) {
            BLwriter.write(lineToBlacklist);
        }
    }

    public void deleteLine(String sourceFile, String lineToRemove) throws IOException {
        File inputFile = new File(sourceFile);
        File tempFile = File.createTempFile("tmp", "");
        File blacklist = new File("Blacklist.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
             BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter BLwriter = new BufferedWriter(new FileWriter(blacklist, true))) {
            addToBlacklist(lineToRemove, BLwriter, blacklist);
            System.out.println(lineToRemove);
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if ((currentLine.trim()).equals(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
        }
        try {
            FileUtils.forceDelete(inputFile);
//            FileUtils.copyFile(chatposts, new File("ChatPosts_Backup.txt"));
//            FileUtils.copyToFile(FileUtils.openInputStream(chatposts), new File("chatposts_backup.txt"));
            FileUtils.forceDelete(new File("chatposts.txt"));
            Boolean success = tempFile.renameTo(inputFile);
            System.out.println("file deleted: " + success);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/ListViewCell.fxml"));
                fxmlLoader.setController(this);
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            text.setText(item.trim());
            setWrapText(true);
            text.wrappingWidthProperty().bind(getListView().widthProperty().subtract(63));
            setText(null);
            setGraphic(gridPane);
        }
    }


}
