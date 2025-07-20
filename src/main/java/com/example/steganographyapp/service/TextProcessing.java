package com.example.steganographyapp.service;

public interface TextProcessing {
    public String bitsToAscii(boolean[] bits);//array of bits to ASCII
    public boolean[] bitStringToBitArray(String bitString);//
    public String binaryToString(String bitString);//принимает String=111000101 выдает String ASCI
    public String toBinary(String message);
    public String fromBinary(String binary);
}
