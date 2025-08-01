module com.example.steganographyapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires org.bytedeco.opencv;
    requires JTransforms;
    requires org.slf4j;
    requires java.desktop;
    requires commons.validator;
    requires java.net.http;


    opens com.example.steganographyapp to javafx.fxml;
    exports com.example.steganographyapp;
}