package com.example.steganographyapp.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

public interface MethodEmbedding {
    public List<double[][]> embeddingMethodOne(List<double[][]> arrayOfBlocks, String text);

    public String extractingMethodOne(List<double[][]> arrayOfBlocks);


}
