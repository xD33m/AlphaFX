package com.sample.ui.mainPanel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class ListViewCellController extends ListCell<String> {

    @FXML
    private Text text;

    @FXML
    private GridPane gridPane = new GridPane();

    @FXML
    private FXMLLoader fxmlLoader;

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
                prefWidthProperty().bind(getListView().widthProperty().subtract(65));

                text.setText(item);
                setWrapText(true);
                text.wrappingWidthProperty().bind(getListView().widthProperty().subtract(65));
                setText(null);
                setGraphic(gridPane);
            }
        }
    }


}
