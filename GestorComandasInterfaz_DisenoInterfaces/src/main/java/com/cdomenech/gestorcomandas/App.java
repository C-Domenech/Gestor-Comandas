package com.cdomenech.gestorcomandas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    /**
     *
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("allOrderTableView"), 1000, 750);
        stage.setScene(scene);
        stage.setTitle("Gestor de comandas");
        Image image = new Image("images/restaurant.png");
        stage.getIcons().add(image);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

}
