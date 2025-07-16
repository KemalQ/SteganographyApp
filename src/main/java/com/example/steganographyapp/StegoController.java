package com.example.steganographyapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StegoController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}