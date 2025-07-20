package com.example.steganographyapp.service.Impl;

import com.example.steganographyapp.service.ImageProcessing;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.jtransforms.dct.DoubleDCT_2D;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

@Data
@ToString(exclude = "image")
public class ImageProcessingImpl implements ImageProcessing {
    private Mat image;

    public Mat photoToMat(String filePath) {//for .png .jpg / .jpeg .bmp .tiff .webp
        // Чтение изображения в цвете (3 канала: BGR)
        Mat matImage = imread(filePath, IMREAD_COLOR);
        if (matImage.empty()){
            throw new RuntimeException("Failed to load image: " + filePath);
        }
        //log.atError();//TODO add logging
        return matImage;
    }

    public Mat getBlueChannel(Mat image) {
        // Создаём список для каналов
        MatVector channels = new MatVector();
        try{
            if (image.empty()){
                throw new RuntimeException("Mat image is empty, check the code flow!");
            }
            // Разделяем изображение на 3 канала (BGR)
            split(image, channels);//TODO задебажить
        }
        catch (Exception e){
            //log.info(e.getMessage());//TODO add logging
        }

        // Возвращаем синий канал (индекс 0 в BGR)
        //var m = matToIntArray(channels.get(0));//TODO experiment check
        return channels.get(0).clone();//использовал клон для исключения проблемы с исчезновением Mat из памяти
    }

    public double[][] matToDoubleArray(Mat blueChannel) {
        int rows = blueChannel.rows();
        int cols = blueChannel.cols();
        double[][] result = new double[rows][cols];

        UByteRawIndexer indexer = blueChannel.createIndexer();
        try{
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result[i][j] = indexer.get(i, j); // Получаем значение по указателю
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            //log.atError();//TODO add logging
        }
        finally {
            indexer.release();//освобождаем память, всегда использовать
        }
        return result;
    }


    public List<double[][]> splitIntoArrayOfBlocks(double[][] channel) {
        int height = channel.length;
        int width = channel[0].length;

        int blocksPerRow = width / 8;
        int blocksPerCol = height / 8;

        List<double[][]> blocks = new ArrayList<>();

        //log.info("Adjusted dimensions to {}x{} (was {}x{})",//TODO add logging
                //blocksPerCol, blocksPerRow, height, width);

        try{
            //TODO проверить разбил ли массив на блоки с шагом 8 блоков
            for (int blockRow = 0; blockRow < blocksPerCol; blockRow++) {
                for (int blockCol = 0; blockCol < blocksPerRow; blockCol++) {
                    double[][] block = new double[8][8];
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            block[i][j] = channel[blockRow * 8 + i][blockCol * 8 + j];
                        }
                    }
                    blocks.add(block);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            //log.atError();
        }

        //log.info("Created {} blocks of 8x8", blocks.size());//TODO add logging

        return blocks;
    }

    public double[][] mergeFromArrayOfBlocks(List<double[][]> blocks, int imageHeight, int imageWidth) {
        double[][] channel = new double[imageHeight][imageWidth];
        int blocksPerRow = imageWidth / 8;

        for (int blockIndex = 0; blockIndex < blocks.size(); blockIndex++) {
            double[][] block = blocks.get(blockIndex);

            int blockRow = blockIndex / blocksPerRow;
            int blockCol = blockIndex % blocksPerRow;

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    channel[blockRow * 8 + i][blockCol * 8 + j] = block[i][j];
                }
            }
        }
        return channel;
    }

    public Mat doubleArrayToMat(double[][] array) {//TODO остался на этом этапе
        int rows = array.length;
        int cols = array[0].length;
        Mat mat = new Mat(rows, cols, CV_64F);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mat.ptr(i, j).putDouble(array[i][j]);
            }
        }
        return mat;
    }

    public Mat doubleArrayToMat2(double[][] array) {//TODO 14.05.2025 degub this code
        int rows = array.length;
        int cols = array[0].length;
        Mat mat = new Mat(rows, cols, CV_8UC1);//created with integer values

        UByteRawIndexer indexer = mat.createIndexer();
        try {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    int value = (int) Math.round(array[i][j]);
                    value = Math.max(0, Math.min(255, value));
                    indexer.put(i, j, value);
                }
            }
        } finally {
            indexer.release();
        }

        return mat;
    }

    public Mat replaceBlueChannel(Mat originalColorImage, Mat newBlueChannel){//TODO 14.05.2025 degub this code
        MatVector channels = new MatVector();
        split(originalColorImage, channels);

        channels.put(0, newBlueChannel); // 0 — синий, 1 — зелёный, 2 — красный

        Mat merged = new Mat();
        merge(channels, merged);
        return merged;
    }

    @Override
    public double[][] dct(double[][] input){
        int rows = input.length;
        int cols = input[0].length;

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                input[i][j] -= 128;//centralization around 0
            }
        }

        // JTransforms требует 2D-массив в виде одномерного массива
        double[][] data = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            System.arraycopy(input[i], 0, data[i], 0, cols);

        // Создаём DCT-объект
        DoubleDCT_2D dct2d = new DoubleDCT_2D(rows, cols);

        // Прямое DCT (in-place)
        dct2d.forward(data, true);
        return data;
    }
    @Override
    public double[][] idct(double[][] input){
        int rows = input.length;
        int cols = input[0].length;

        // JTransforms требует 2D-массив в виде одномерного массива
        double[][] data = new double[rows][cols];
        for (int i = 0; i < rows; i++)
            System.arraycopy(input[i], 0, data[i], 0, cols);

        // Создаём DCT-объект
        DoubleDCT_2D dct2d = new DoubleDCT_2D(rows, cols);

        // Обратное DCT
        dct2d.inverse(data, true);

        for (int i = 0; i < data.length; i++) {//TODO you can delete this block if don't want centralization
            for (int j = 0; j < data[0].length; j++) {
                data[i][j] += 128;//decentralization. 0-255
                data[i][j] = Math.min(255, Math.max(0, data[i][j])); //защита от выхода за границы
            }
        }
        return data;
    }
}
