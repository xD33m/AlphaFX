package sample;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import sample.db.DataSource;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        new Controller().loadWindow("sample/login/login.fxml", "Login");
    }


    public static void main(String[] args) {

        DataSource.getInstance().insertUser("namef", "emafil", "passwofrd");
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
        DataSource.getInstance().close();
    }
}
