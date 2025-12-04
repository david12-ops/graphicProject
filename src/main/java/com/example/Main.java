package com.example;

import com.example.view.MainScreen;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    // Zatim požít Swing a pak z toho zkusit udělat malování
    @Override
    public void start(Stage primaryStage) {
        MainScreen mainScreen = new MainScreen(primaryStage);

        Scene scene = new Scene(mainScreen, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
