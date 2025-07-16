package com.example.steganographyapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StegoApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(StegoApplication.class.getResource("mainWindow.fxml"));
        FXMLLoader fxmlLoader2 = new FXMLLoader(StegoApplication.class.getResource("cipherApp.fxml"));
        Scene scene = new Scene(fxmlLoader2.load(), 520, 440);
        stage.setTitle("SteganographyApp");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}