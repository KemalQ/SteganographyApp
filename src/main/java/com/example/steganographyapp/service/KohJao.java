package com.example.steganographyapp.service;

public interface KohJao {
    public double[][] embedBitInBlock(double[][] block, boolean bit);
    public Boolean extractBitFromBlock(double[][] blockIn);
    public static double[][] deepCopy(double[][] original) {//TODO delete after testing
        if (original == null) return null;
        double[][] copy = new double[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
}
