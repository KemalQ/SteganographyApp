module com.example.steganographyapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires org.bytedeco.opencv;
    requires JTransforms;


    opens com.example.steganographyapp to javafx.fxml;
    exports com.example.steganographyapp;
}