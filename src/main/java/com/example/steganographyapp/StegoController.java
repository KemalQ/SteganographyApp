package com.example.steganographyapp;

import com.example.steganographyapp.service.Impl.ImageProcessingImpl;
import com.example.steganographyapp.service.Impl.MethodEmbeddingImpl;
import com.example.steganographyapp.service.Impl.SecureRandomStringGeneratorImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

public class StegoController {
    @FXML private CheckMenuItem showPropertiesMenuItem;
    @FXML private CheckMenuItem showStatusBarMenuItem;

    @FXML private TabPane mainTabPane;

    //ENCODE

    @FXML private ImageView originalImageView;
    @FXML private TextArea textToHideArea;
    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private Button openImageButton;
    @FXML private Button clearImageButton;
    @FXML private Label encodeStatusLabel;

    @FXML private Region imageIndicator;
    @FXML private Region textIndicator;
    @FXML private Region algorithmIndicator;
    @FXML private Region startDateIndicator;
    @FXML private Region endDateIndicator;


    //DECODE
    @FXML private ImageView decodeImageView;
    @FXML private ComboBox<String> decodeAlgorithmComboBox;
    @FXML private Button openDecodeImageButton;
    @FXML private Button clearDecodeImageButton;



    @FXML private Button previewButton;
    @FXML private Button saveEncodedButton;

    @FXML private Region decodeDropZone;

    @FXML private Button decodeButton;

    @FXML private TextArea extractedTextArea;
    @FXML private Button copyTextButton;
    @FXML private Button saveTextButton;

    @FXML private ImageView analysisImageView;
    @FXML private Button analyzeButton;
    @FXML private Button generateReportButton;
    @FXML private VBox analysisResultsContainer;

    @FXML private Label fileSizeLabel;
    @FXML private Label dimensionsLabel;
    @FXML private Label formatLabel;
    @FXML private Label capacityLabel;
    @FXML private Label statusLabelBottom;

    @FXML private VBox propertiesPanel;

    @FXML private VBox parametersContainer;

    @FXML private Button encodeButton;
    @FXML private Button resetButton;

    @FXML private Label imageNameLabel;
    @FXML private Label imageSizeLabel;
    @FXML private Label imageTypeLabel;
    @FXML private Label imageDimensionsLabel;

    @FXML private Label progressLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label memoryLabel;

    private Stage stage;
    private Image image;

    private String name, path;
    private int width, height;
    private WritableImage outputimage;

    private final ImageProcessingImpl imageProcessing = new ImageProcessingImpl();//TODO delete final
    private final MethodEmbeddingImpl methodEmbedding = new MethodEmbeddingImpl();//TODO delete final
    private final SecureRandomStringGeneratorImpl secureRandomStringGenerator = new SecureRandomStringGeneratorImpl();//TODO delete final

    @FXML
    public void openImage(ActionEvent event) {//completed
        FileChooser fileChooser = new FileChooser();//новое окно для выбора
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg,", "*.jpeg"));//png tur dosya secimi
        File selectedFile = fileChooser.showOpenDialog(stage);// .png formatlı resimleri gösteriyor

        if (selectedFile != null) {

            image = new Image(selectedFile.toURI().toString());
            originalImageView.setImage(image);
            width = (int) image.getWidth();
            height = (int) image.getHeight();
            name = selectedFile.getName();
            imageNameLabel.setText("Name: " + name);
            imageSizeLabel.setText("Size: " + width + "x" + height + "px");

            long bytes = selectedFile.length();
            double megabytes = bytes / (1024.0 * 1024.0);
            imageSizeLabel.setText("Size: " + megabytes + " MB");


        }
    }//completed

    @FXML
    public void setClearImageButton(ActionEvent event) {//completed
        if (originalImageView.getImage() != null) {
            setDefaultImage(true);
        }
    }

    public void openDecodeImage(ActionEvent event) {//completed
        FileChooser fileChooser = new FileChooser();//новое окно для выбора
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg,", "*.jpeg"));//png tur dosya secimi
        File selectedFile = fileChooser.showOpenDialog(stage);// .png formatlı resimleri gösteriyor

        if (selectedFile != null) {

            image = new Image(selectedFile.toURI().toString());
            decodeImageView.setImage(image);
            width = (int) image.getWidth();
            height = (int) image.getHeight();
            name = selectedFile.getName();
            imageNameLabel.setText("Name: " + name);
            imageSizeLabel.setText("Size: " + width + "x" + height + "px");

            long bytes = selectedFile.length();
            double megabytes = bytes / (1024.0 * 1024.0);
            imageSizeLabel.setText("Size: " + megabytes + " MB");


        }
    }//completed
    @FXML
    public void setClearDecodeImageButton(ActionEvent event) {//completed
        if (decodeImageView.getImage() != null) {
            setDefaultImage(false);
        }
    }

    @FXML
    public void encodeImage(ActionEvent event) {
        if (!validateInputs()) {
            encodeStatusLabel.setText("Please correct input fields.");
            return;
        }

        File imageFile = new File(image.getUrl());
        Mat mat = imageProcessing.photoToMat(imageFile.getAbsolutePath());
        Mat blueChannel = imageProcessing.getBlueChannel(mat);
        double[][] array = imageProcessing.matToDoubleArray(blueChannel);
        List<double[][]> arrayOfBlocks = imageProcessing.splitIntoArrayOfBlocks(array);
        arrayOfBlocks = methodEmbedding.embeddingMethodOne(arrayOfBlocks, textToHideArea.getText());
        double[][] imageArray = imageProcessing.mergeFromArrayOfBlocks(arrayOfBlocks, array.length, array[0].length);
        Mat modifiedBlue = imageProcessing.doubleArrayToMat2(imageArray);
        Mat finalImage = imageProcessing.replaceBlueChannel(mat, modifiedBlue);

        String path = System.getProperty("user.home") + "/Desktop/OutputFiles/" + secureRandomStringGenerator.generateRandomString(16) + ".png";
        imwrite(path, finalImage);
        encodeStatusLabel.setText("Image saved: " + path);

//        File imageFile = new File(image.getUrl());
//        validateInputs();
//
//        Mat mat = imageProcessing.photoToMat(imageFile.getAbsolutePath());// returns mat object
//        Mat blueChannel = imageProcessing.getBlueChannel(mat);//returns blue channel
//        double[][] array = imageProcessing.matToDoubleArray(blueChannel);//returns double[][]
//        List<double[][]> arrayOfBlocks = imageProcessing.splitIntoArrayOfBlocks(array);//transforms double[][] to List<double[][]>
//
//        arrayOfBlocks = methodEmbedding.embeddingMethodOne(arrayOfBlocks, "Checking ukrainian symbols, ну коли вже");
//
//        double[][] imageArray = imageProcessing.mergeFromArrayOfBlocks(arrayOfBlocks, array.length, array[0].length);//TODO 14.05.2025 debug
//
//        Mat modifiedBlue = imageProcessing.doubleArrayToMat2(imageArray);//TODO 14.05.2025 debug
//
//        Mat finalImage = imageProcessing.replaceBlueChannel(mat, modifiedBlue);//TODO 14.05.2025 debug
//
//        //TODO this part is for saving, edit it to save in stage
//        String path = System.getProperty("user.home") + "/Desktop/OutputFiles/"+ secureRandomStringGenerator.generateRandomString(16) + ".png";
//        imwrite(path, finalImage);
//        System.out.println("Saved: " + path);
    }

    private boolean validateInputs() {
        boolean valid = true;

        // Image
        if (originalImageView.getImage() == null || isDefaultImage(originalImageView.getImage())) {
            imageIndicator.getStyleClass().setAll("indicator-red");
            valid = false;
        } else {
            imageIndicator.getStyleClass().setAll("indicator-green");
        }

        // Text
        if (textToHideArea.getText().isEmpty()) {
            textIndicator.getStyleClass().setAll("indicator-red");
            valid = false;
        } else {
            textIndicator.getStyleClass().setAll("indicator-green");
        }

        // Algorithm
        if (algorithmComboBox.getValue() == null) {
            algorithmIndicator.getStyleClass().setAll("indicator-red");
            valid = false;
        } else {
            algorithmIndicator.getStyleClass().setAll("indicator-green");
        }

        // Start Date
        if (startDate.getValue() == null) {
            startDateIndicator.getStyleClass().setAll("indicator-red");
            valid = false;
        } else {
            startDateIndicator.getStyleClass().setAll("indicator-green");
        }

        // End Date
        if (endDate.getValue() == null) {
            endDateIndicator.getStyleClass().setAll("indicator-red");
            valid = false;
        } else {
            endDateIndicator.getStyleClass().setAll("indicator-green");
        }

        return valid;
    }

    private boolean isDefaultImage(Image img) {
        String defaultUrl = getClass().getResource("/com/example/steganographyapp/Images/UploadImage.png").toExternalForm();
        return img.getUrl().equals(defaultUrl);
    }

    @FXML
    public void resetAllfieldsEncode(){
        if (!isDefaultImage(originalImageView.getImage())){
            setDefaultImage(true);//for encode
        }
        textToHideArea.setText("");
        algorithmComboBox.setPromptText("Select encryption method");
        startDate.setValue(null);
        endDate.setValue(null);
    }


    @FXML
    public void decodeImage(ActionEvent event) {

    }

    @FXML
    public void saveImage(ActionEvent event) {
        // ...newProject
    }

    private void setComboBox(){//completed
        ObservableList<String> observableList = FXCollections.observableArrayList("Algorithm 1", "Algorithm 2", "Algorithm 3");
        algorithmComboBox.setItems(observableList);
        decodeAlgorithmComboBox.setItems(observableList);
    }
    @FXML
    public void initialize() {
        setDefaultImage(true);//for encode
        setDefaultImage(false);
        setComboBox();
    }

    public void setDefaultImage(Boolean index) {//completed
        Image defaultImage = new Image(getClass().getResource("/com/example/steganographyapp/Images/UploadImage.png").
                toExternalForm());
        if (index == true){
            originalImageView.setImage(defaultImage);
        }
        else decodeImageView.setImage(defaultImage);
    }
    @FXML
    public void takingStartDate(){//completed
        LocalDate start  = startDate.getValue();

        if (start != null) {
            endDate.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setDisable(empty || item.isBefore(start));//disable previous dates
                }
            });
        }
    }
    @FXML
    public void takingEndDate(){//completed
        LocalDate end = endDate.getValue();
    }
}

/*
    @FXML
    public void exitApplication(ActionEvent event) {
        // ...newProject
    }

    @FXML
    public void undo(ActionEvent event) {
        // ...newProject
    }

    @FXML
    public void redo(ActionEvent event) {
        // ...newProject
    }

    @FXML
    public void showPreferences(ActionEvent event) {
        // ...newProject
    }

    @FXML
    public void zoomIn(ActionEvent event) {
        // ...newProject
    }

    @FXML
    public void zoomOut(ActionEvent event) {
        // ...newProject
    }

    @FXML
    public void fitToWindow(ActionEvent event) {
        // ...newProject
    }

    @FXML
    public void openBatchProcessing(ActionEvent event) {
        // ...newProject
    }

    @FXML
    public void analyzeImage(ActionEvent event) {
        // ...openConverter
    }

    @FXML
    public void openConverter(ActionEvent event) {
        // ...
    }

    @FXML
    public void openDocumentation(ActionEvent event) {
        // ...openConverter
    }

    @FXML
    public void showAbout(ActionEvent event) {
        // ...openConverter
    }

    @FXML
    public void handleDragOver(ActionEvent event) {
        // ...openConverter
    }

    @FXML
    public void handleDragDropped(ActionEvent event) {
        // ...openConverter
    }

    @FXML
    public void handleDecodeDragOver(ActionEvent event) {
        // ...openConverter
    }

    @FXML
    public void handleDecodeDragDropped(ActionEvent event) {
        // ...openConverter
    }
* */