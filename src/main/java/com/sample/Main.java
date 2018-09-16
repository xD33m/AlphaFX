package com.sample;

import com.sample.db.DataSource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class Main extends Application {

    private static void setup(File file) throws IOException {
        System.out.println(System.getenv("APPDATA"));
        // File file = new File("C:\\Users\\" + username + "\\Documents\\", "data.txt");
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            System.out.println("creating file");
            if (file.createNewFile()) {
                System.out.println("Successfully created file");
            } else {
                System.out.println("Failed to create file");
            }
        }
    }

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
        File file1 = new File(System.getenv("APPDATA") + "\\DofusChat\\text\\", "Blacklist.txt");
        File file2 = new File(System.getenv("APPDATA") + "\\DofusChat\\text\\", "chatposts.txt");
        File file3 = new File(System.getenv("APPDATA") + "\\DofusChat\\text\\", "PlayerBuys.txt");
        File file4 = new File(System.getenv("APPDATA") + "\\DofusChat\\text\\", "PlayerSells.txt");
        File file5 = new File(System.getenv("APPDATA") + "\\DofusChat\\text\\", "wtb.txt");
        File file6 = new File(System.getenv("APPDATA") + "\\DofusChat\\text\\", "wts.txt");
        File file7 = new File(System.getenv("APPDATA") + "\\DofusChat\\", "userToken");
        File file8 = new File(System.getenv("APPDATA") + "\\DofusChat\\", "session");
        try {
            setup(file1);
            setup(file2);
            setup(file3);
            setup(file4);
            setup(file5);
            setup(file6);
            setup(file7);
            setup(file8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!DataSource.getInstance().open()) {
            System.out.println("DB offline");
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