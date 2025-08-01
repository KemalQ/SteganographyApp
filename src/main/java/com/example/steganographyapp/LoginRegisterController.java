package com.example.steganographyapp;


import com.example.steganographyapp.utils.HttpUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.net.URL;


import java.net.http.HttpResponse;
import java.util.ResourceBundle;

@Slf4j
public class LoginRegisterController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Hyperlink logInLink;

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField loginUsername;

    @FXML
    private CheckBox rememberMe;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button signUpButton;

    @FXML
    private TextField signUpEmail;

    @FXML
    private Hyperlink signUpLink;

    @FXML
    private PasswordField signUpPassword;

    @FXML
    private PasswordField signUpRepeatPassword;

    @FXML
    private TextField signUpUsername;

    @FXML
    private VBox loginPane;
    
    @FXML
    private VBox signUpPane;

    @FXML
    private void switchToSignUp() {
        loginPane.setVisible(false);
        signUpPane.setVisible(true);
    }

    @FXML
    private void switchToLogin() {
        signUpPane.setVisible(false);
        loginPane.setVisible(true);
    }

    @FXML
    private void onLoginButtonClick() {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        String jsonBody = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);
        try {
            HttpResponse<String> response = HttpUtil.sendPost("http://localhost:8080/api/auth/login", jsonBody);
            if (response.statusCode() == 200) {
                System.out.println("Login successful: " + response.body());
                switchToMainWindow();
                // можно вызвать переход на главное окно
            } else {
                showError("Login failed: " + response.body());
            }
        } catch (Exception e) {
            showError("Error connecting to server: " + e.getMessage());
        }
    }

    @FXML
    private void onSignUpButtonClicked(){
        String username = signUpUsername.getText();
        String email = signUpEmail.getText();
        String password = signUpPassword.getText();
        String repeatPassword = signUpRepeatPassword.getText();

        if (username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || email.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        } else if (!password.equals(repeatPassword)) {
            showError("Passwords do not match.");
            return;
        } else if (!EmailValidator.getInstance().isValid(email)) {
            showError("Email is not valid.");
            return;
        }

        String jsonBody = String.format("{\"username\":\"%s\", \"email\":\"%s\", \"password\":\"%s\"}", username, email, password);
        try {
            HttpResponse<String> response = HttpUtil.sendPost("http://localhost:8080/api/auth/register", jsonBody);
            if (response.statusCode() == 201) {
                System.out.println("Registration successful: " + response.body());
                switchToMainWindow();
                // возможно, переход на экран логина
            } else if (response.statusCode() == 200){
                System.out.println("Registration successful: " + response.body());
            } else if (response.statusCode() == 202){
                System.out.println("Registration successful: " + response.body());
            }
            else {
                showError("Registration failed: " + response.body());
            }
        } catch (Exception e) {
            showError("Error connecting to server: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        log.info(message);
    }

    private void switchToMainWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/SecondMainW.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Main Dashboard");
            stage.setScene(new Scene(root));
            stage.show();

            // Закрываем текущее окно (если нужно)
            Stage currentStage = (Stage) signUpButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load main window: " + e.getMessage());
        }
    }

}
