package com.sample;


import com.sample.db.DataSource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        new Controller().loadWindow("fxml/main.fxml", "Main");
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {

        super.init();
        if(!DataSource.getInstance().open()) {
            System.out.println("FATAL ERROR: Couldn't connect to db");
            Platform.exit();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.setImplicitExit(false);
        DataSource.getInstance().close();
    }
}
