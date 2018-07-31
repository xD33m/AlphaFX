package sample;


import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private double xOffset = 0;
    private double yOffset = 0;


    @Override
    public void start(Stage primaryStage) {
        new Controller().loadWindow("sample/login/login.fxml", "Login");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
