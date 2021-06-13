package com.wisecode.core.util;

import lombok.extern.log4j.Log4j2;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Log4j2
public class SystemUtil {
    private static final String key = "Ghassan_Kahool";

    private static SecretKeySpec secretKey;
    private static byte[] keyArray;

    static {
        setKey(key);
    }

    public static void setKey(String myKey) {
        MessageDigest sha = null;
        try {
            keyArray = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            keyArray = sha.digest(keyArray);
            keyArray = Arrays.copyOf(keyArray, 16);
            secretKey = new SecretKeySpec(keyArray, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return new String(Base64.getUrlEncoder().encode(encrypted));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] original = cipher.doFinal(Base64.getUrlDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }
}
