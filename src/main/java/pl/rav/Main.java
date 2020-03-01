package pl.rav;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("fileFXML.fxml"));

        stage.setTitle("FX converter");
        stage.setScene(new Scene(root, 450, 600)); //jak automatycznie dopasować rozmiar do ilości elementów / czy da się skalować ?
        
        
        // Mozna trzeba skorzystac z metody sizeToScene()
        
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}