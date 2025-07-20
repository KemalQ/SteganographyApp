package com.example.steganographyapp.service.Impl;

import com.example.steganographyapp.service.KohJao;

public class KohJaoImpl implements KohJao {
    /**
     * Встраивает один бит информации в блок изображения 8x8 пикселей
     * с помощью алгоритма Коха-Жао.
     *
     * @param block блок пикселей 8x8
     * @param bit бит для встраивания (true = 1, false = 0)
     * @return блок с встроенным битом информации
     */

    private final ImageProcessingImpl imageProcessing;

    public KohJaoImpl(ImageProcessingImpl imageProcessing) {
        this.imageProcessing = imageProcessing;
    }

    public double[][] embedBitInBlock(double[][] block, boolean bit) {
        //System.out.println("Matrix before DCT:");//TODO delete after checking
        //printMatrix(block);//TODO delete after checking
        // 1. Применяю DCT к блоку 8x8
        double[][] dctCoefficients = imageProcessing.dct(block);
        //System.out.println("DCT coefficents in KohJao:");//TODO delete after checking
        //printMatrix(dctCoefficients);//TODO delete after checking
        // 2. Рассчитываю разницу между выбранными коэффициентами
        double k = Math.abs(dctCoefficients[4][5]) - Math.abs(dctCoefficients[5][4]);

        // 3. Модифицирую коэффициенты в зависимости от бита
        if (bit) { // Если нужно встроить 1
            if (k <= 15) {
                // Первый коэффициент делаем существенно больше второго
                double sign = dctCoefficients[4][5] >= 0 ? 1.0 : -1.0;
                dctCoefficients[4][5] = sign * (Math.abs(dctCoefficients[5][4]) + 35);
            }
        } else { // Если нужно встроить 0
            if (k >= -15) {
                // Второй коэффициент делаю существенно больше первого
                double sign = dctCoefficients[5][4] >= 0 ? 1.0 : -1.0;
                dctCoefficients[5][4] = sign * (Math.abs(dctCoefficients[4][5]) + 35);
            }
        }

        //System.out.println("After embedding: " + bit);//TODO delete after checking
        //printMatrix(dctCoefficients);//TODO delete after checking
        // 4. Применил обратное DCT для получения модифицированного блока
        return imageProcessing.idct(dctCoefficients);
    }

    /**
     * exctract one bit from 8x8 block.
     * @param //block блок пикселей 8x8
     * @return extracted bit (true = 1, false = 0) or null if impossible to extract
     */
    public Boolean extractBitFromBlock(double[][] blockIn) {
        double[][] blockCopy = KohJao.deepCopy(blockIn);   //TODO delete after testing
        // 1. Применил DCT к блоку 8x8
        double[][] dctCoefficients = imageProcessing.dct(blockCopy);

        // 2. Рассчитал разницу между коэффициентами
        double k = Math.abs(dctCoefficients[4][5]) - Math.abs(dctCoefficients[5][4]);

        // 3. Интерпретировал разницу как бит
        if (k >= 15) {
            return true;  // бит = 1
        }
        else if (k <= -15) {
            return false; // бит = 0
        }
        else {
            return null;  // не смог определить бит
        }
    }
}