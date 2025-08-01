package com.example.steganographyapp.service.Impl;

import com.example.steganographyapp.service.MethodEmbedding;
import com.example.steganographyapp.service.KohJao;
import com.example.steganographyapp.service.TextProcessing;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MethodEmbeddingImpl implements MethodEmbedding {
    TextProcessing textProcessing = new TextProcessingImpl();
    KohJao kohJao = new KohJaoImpl(new ImageProcessingImpl());
    @Override
    public List<double[][]> embeddingMethodOne(List<double[][]> arrayOfBlocks, String text) {

        String binaryMessage = textProcessing.toBinary(text);
        String binaryLength = String.format("%10s", Integer.toBinaryString(binaryMessage.length())).replace(' ', '0');

        // 1. флаг наличия сообщения
        for (int i = 0; i < 3; i++){
            arrayOfBlocks.set(i, kohJao.embedBitInBlock(arrayOfBlocks.get(i), true));
        }
        // 2. Внедряю длину сообщения (в битах)
        for (int i = 0; i < 10; i++) {
            int bit = Character.getNumericValue(binaryLength.charAt(i));
            if (bit==0){
                arrayOfBlocks.set(3 + i, kohJao.embedBitInBlock(arrayOfBlocks.get(3 + i), false));//starts from 3 block
            }
            else arrayOfBlocks.set(3+ i, kohJao.embedBitInBlock(arrayOfBlocks.get(3 + i), true));//starts from 3 block
        }

        // 3. Внедряею само сообщение
        for (int i = 0; i < binaryMessage.length(); i++) {
            int bit = Character.getNumericValue(binaryMessage.charAt(i));
            if (bit==0){
                arrayOfBlocks.set(3 + 10 + i, kohJao.embedBitInBlock(arrayOfBlocks.get(3 + 10 + i), false));
            }
            else arrayOfBlocks.set(3 + 10 + i, kohJao.embedBitInBlock(arrayOfBlocks.get(3 + 10 + i), true));
        }

        return arrayOfBlocks;
    }

    @Override
    public String extractingMethodOne(List<double[][]> arrayOfBlocks) {
        //1. Чтение флага "сообщение есть"
        int trueCount = 0;
        for (int i = 0; i < 3; i++) {
            Boolean bit = kohJao.extractBitFromBlock(arrayOfBlocks.get(i));
            if (bit != null && bit) {
                trueCount++;
            }
        }

        log.info("MethodEmbedding - extractingMethodOne - trueCount: " + trueCount);//TODO delete after checking 01.06.2025
        if (trueCount >= 2) {// флаг подтверждён
            //2. Чтение длины
            StringBuilder lengthBinary = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                Boolean bit = kohJao.extractBitFromBlock(arrayOfBlocks.get(3 + i));
                if (bit == null) return null;
                lengthBinary.append(bit ? '1' : '0');
            }
            int messageLength = Integer.parseInt(lengthBinary.toString(), 2);// получил длину внедренного сообщения в int
            log.info("MethodEmbedding - extractingMethodOne - messageLength: " + messageLength);//TODO delete after checking 01.06.2025

            // 3. Чтение сообщения
            StringBuilder binaryMessage = new StringBuilder();
            for (int i = 0; i < messageLength; i++) {
                Boolean bit = kohJao.extractBitFromBlock(arrayOfBlocks.get(3 + 10 + i));
                if (bit == null) return null;
                binaryMessage.append(bit ? '1' : '0');
            }
            log.info("MethodEmbedding - extractingMethodOne - fromBinaryToStringMessage: " + textProcessing.fromBinary(binaryMessage.toString()));//TODO delete after checking 01.06.2025
            // Расшифровка бинарного текста в строку
            return textProcessing.fromBinary(binaryMessage.toString());
        } else return null;
    }
}
