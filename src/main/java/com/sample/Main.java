package com.sample;

import com.sample.db.DataSource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class Main extends Application {

    public static final String APPDATATEXTPATH = System.getenv("APPDATA") + "\\DofusChat\\text\\";
    public static final String APPDATAPATH = System.getenv("APPDATA") + "\\DofusChat\\";
    public static final String APPDATATEMPPATH = System.getenv("APPDATA") + "\\DofusChat\\text\\tmp";
    public static final String PlayerSellsTXT = System.getenv("APPDATA") + "\\DofusChat\\text\\PlayerSells.txt";
    public static final String PlayerBuysTXT = System.getenv("APPDATA") + "\\DofusChat\\text\\PlayerBuys.txt";
    public static final String BlacklistPATH = System.getenv("APPDATA") + "\\DofusChat\\text\\Blacklist.txt";
    public static final String ChatpostsPATH = System.getenv("APPDATA") + "\\DofusChat\\text\\chatposts.txt";
    public static final String WTBPATH = System.getenv("APPDATA") + "\\DofusChat\\text\\wtb.txt";
    public static final String WTSPATH = System.getenv("APPDATA") + "\\DofusChat\\text\\wts.txt";
    public static final String UserTokenPATH = System.getenv("APPDATA") + "\\DofusChat\\userToken";
    public static final String SessionPATH = System.getenv("APPDATA") + "\\DofusChat\\session";

    private static void setup(File file) throws IOException {
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
        File file1 = new File(APPDATATEXTPATH, "Blacklist.txt");
        File file2 = new File(APPDATATEXTPATH, "chatposts.txt");
        File file3 = new File(APPDATATEXTPATH, "PlayerBuys.txt");
        File file4 = new File(APPDATATEXTPATH, "PlayerSells.txt");
        File file5 = new File(APPDATATEXTPATH, "wtb.txt");
        File file6 = new File(APPDATATEXTPATH, "wts.txt");
        File file7 = new File(APPDATAPATH, "userToken");
        File file8 = new File(APPDATAPATH, "session");
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