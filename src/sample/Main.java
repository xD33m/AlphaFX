package sample;

import com.jfoenix.controls.JFXDecorator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample/windows/register.fxml"));

        JFXDecorator decorator = new JFXDecorator(primaryStage, root, false, false, true);
        decorator.setCustomMaximize(true);
        String uri = getClass().getClassLoader().getResource("sample/styles/dark.theme.css").toExternalForm();
//        String uri = getClass().getResource("mainWindowDecorator.css").toExternalForm();
        Scene scene = new Scene(decorator);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        scene.getStylesheets().add(uri);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
