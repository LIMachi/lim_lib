package com.limachi.lim_lib;

public class Maths {
    public static int clampModulus(int val, int base, int modulus) {
        while (val < base)
            val += modulus;
        while (val >= base + modulus)
            val -= modulus;
        return val;
    }
}
