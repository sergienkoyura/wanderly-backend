package com.wanderly.authservice.util;

import java.util.Random;

public class CodeGeneratorUtil {
    public static String generateVerificationCode() {
        int code = 100_000 + new Random().nextInt(900_000);
        return String.valueOf(code);
    }
}