package com.example.steganographyapp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private CheckBox checkBox;

    @FXML
    private Button loginButton;

    @FXML
    private Label loginLabel;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    void initialize() {
        assert checkBox != null : "fx:id=\"checkBox\" was not injected: check your FXML file 'loginWindow.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'loginWindow.fxml'.";
        assert loginLabel != null : "fx:id=\"loginLabel\" was not injected: check your FXML file 'loginWindow.fxml'.";
        assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'loginWindow.fxml'.";
        assert username != null : "fx:id=\"username\" was not injected: check your FXML file 'loginWindow.fxml'.";

    }

}