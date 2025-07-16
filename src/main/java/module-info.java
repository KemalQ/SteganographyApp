module com.example.steganographyapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.steganographyapp to javafx.fxml;
    exports com.example.steganographyapp;
}