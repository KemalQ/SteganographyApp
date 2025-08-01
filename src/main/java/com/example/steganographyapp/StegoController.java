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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

public class StegoController {
    @FXML private CheckMenuItem showPropertiesMenuItem;
    @FXML private CheckMenuItem showStatusBarMenuItem;

    @FXML private TabPane mainTabPane;

    //ENCODE

    @FXML private ImageView originalImageView;
    @FXML private ImageView encodedImageView;
    @FXML private TextArea textToHideArea;
    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private Button openImageButton;
    @FXML private Button clearImageButton;
    @FXML private Label encodeStatusLabel;
    @FXML private Button saveButton;

    @FXML private Region textIndicator;
    @FXML private Region algorithmIndicator;
    @FXML private Region imageIndicator;
    @FXML private Region startDateIndicator;
    @FXML private Region endDateIndicator;



    //DECODE
    @FXML private ImageView decodeImageView;
    @FXML private ComboBox<String> decodeAlgorithmComboBox;
    @FXML private Button openDecodeImageButton;
    @FXML private Button clearDecodeImageButton;
    @FXML private Button decodeButton;
    @FXML private TextArea extractedTextArea;
    @FXML private Button copyTextButton;
    @FXML private Button saveTextButton;
    @FXML private Label decodeStatusLabel;


    @FXML private Button previewButton;
    @FXML private Button saveEncodedButton;

    @FXML private Region decodeDropZone;


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
    private Mat processedImage;

    private final ImageProcessingImpl imageProcessing = new ImageProcessingImpl();//TODO delete final
    private final MethodEmbeddingImpl methodEmbedding = new MethodEmbeddingImpl();//TODO delete final
    private final SecureRandomStringGeneratorImpl secureRandomStringGenerator = new SecureRandomStringGeneratorImpl();//TODO delete final

    //TODO ENCODE
    @FXML
    public void openImage(ActionEvent event) {//completed
        FileChooser fileChooser = new FileChooser();//новое окно для выбора
        fileChooser.setTitle("Open Image Files");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));//png tur dosya secimi
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
    private void handleImageViewClick(MouseEvent event) {
        //openImage(event);
    }

    @FXML
    public void setClearImageButton(ActionEvent event) {//completed
        if (originalImageView.getImage() != null) {
            setDefaultImage(true);
        }
    }

    @FXML
    public void encodeImage(ActionEvent event) {
        if (!validateInputs()) {
            encodeStatusLabel.setText("Please correct input fields.");
            return;
        }

        //File imageFile = new File(image.getUrl());
        File imageFile;
        try {
            imageFile = new File(new URI(image.getUrl()));
        } catch (URISyntaxException e) {
            encodeStatusLabel.setText("Invalid image URL format.");
            return;
        }

        Mat mat = imageProcessing.photoToMat(imageFile.getAbsolutePath());
        Mat blueChannel = imageProcessing.getBlueChannel(mat);
        double[][] array = imageProcessing.matToDoubleArray(blueChannel);
        List<double[][]> arrayOfBlocks = imageProcessing.splitIntoArrayOfBlocks(array);
        arrayOfBlocks = methodEmbedding.embeddingMethodOne(arrayOfBlocks, textToHideArea.getText());
        double[][] imageArray = imageProcessing.mergeFromArrayOfBlocks(arrayOfBlocks, array.length, array[0].length);
        Mat modifiedBlue = imageProcessing.doubleArrayToMat2(imageArray);
        Mat finalImage = imageProcessing.replaceBlueChannel(mat, modifiedBlue);

        this.processedImage = finalImage;
        encodeStatusLabel.setText("Image encoded successfully. Click Save to save the file.");

        if (processedImage != null){
            encodedImageView.setImage(matToImage(processedImage));
        }
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
        encodeStatusLabel.setText("Ready to encode");
    }

    @FXML
    public void saveImage(ActionEvent even){
        if (processedImage == null){
            encodeStatusLabel.setText("No encoded image to savePlease encode an image first.");
            return;
        }
        // Показываем диалог выбора места сохранения
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Encoded Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );
        fileChooser.setInitialFileName(secureRandomStringGenerator.generateRandomString(16) + ".png");

        File saveFile = fileChooser.showSaveDialog(stage);

        if (saveFile != null) {
            try {
                String savePath = saveFile.getAbsolutePath();
                if (!savePath.endsWith(".png")) {
                    savePath += ".png";
                }

                boolean success = imwrite(savePath, processedImage);

                if (success) {
                    encodeStatusLabel.setText("Image saved successfully: " + savePath);
                } else {
                    encodeStatusLabel.setText("Failed to save image.");
                }
            } catch (Exception e) {
                encodeStatusLabel.setText("Error saving image: " + e.getMessage());
            }
        }
    }

    //TODO DECODE
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
    public void setClearDecodeImageButton(ActionEvent event) {
        if (decodeImageView.getImage() != null) {
            setDefaultImage(false);
        }
    }//completed

    @FXML
    public void decodeText(ActionEvent event) {
        if (image == null || image.getUrl() == null) {
            decodeStatusLabel.setText("Please select an image first.");
            return;
        }

        File imageFileDecode;
            try {
                imageFileDecode = new File(new URI(image.getUrl()));
            } catch (URISyntaxException e) {
                decodeStatusLabel.setText("Invalid image URL format.");
                return;
            }

            Mat matDecode = imageProcessing.photoToMat(imageFileDecode.getAbsolutePath());// returns mat object
            Mat blueChannelDecode = imageProcessing.getBlueChannel(matDecode);//returns blue channel
            double[][] arrayDecode = imageProcessing.matToDoubleArray(blueChannelDecode);//returns double[][]
            List<double[][]> arrayOfBlocksDecode = imageProcessing.splitIntoArrayOfBlocks(arrayDecode);
            String decodedText = methodEmbedding.extractingMethodOne(arrayOfBlocksDecode);

        if (decodedText == null || decodedText.isBlank()) {
            decodeStatusLabel.setText("No hidden message found or failed to extract.");
            extractedTextArea.clear();
        } else {
            extractedTextArea.setText(decodedText);
            decodeStatusLabel.setText("Message successfully extracted.");
        }

    }

    @FXML
    public void decodeImage(ActionEvent event) {

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

    public static Image matToImage(Mat mat){
        BytePointer bytePointer = new BytePointer();
        opencv_imgcodecs.imencode(".png", mat, bytePointer);

        byte[] bytes = new byte[(int)bytePointer.limit()];
        bytePointer.get(bytes);

        return new Image(new ByteArrayInputStream(bytes));
    }
}