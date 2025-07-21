package com.example.steganographyapp.service.Impl;

import com.example.steganographyapp.service.MethodEmbedding;
import com.example.steganographyapp.service.KohJao;
import com.example.steganographyapp.service.TextProcessing;

import java.util.List;

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
        return "";
    }
}
