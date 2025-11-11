package com.finance.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public final class EncryptionUtil {
    // AES-128 GCM demonstration key handling
    private static final String AES = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int IV_LENGTH_BYTES = 12;

    // For demo purposes only. In production, load from env/secret storage.
    private static final byte[] DEFAULT_KEY_BYTES = "pftracker-demo-k".getBytes(StandardCharsets.UTF_8); // 16 bytes

    private EncryptionUtil() {
    }

    public static String encrypt(String plaintext) {
        return encrypt(plaintext, DEFAULT_KEY_BYTES);
    }

    public static String decrypt(String ciphertextBase64) {
        return decrypt(ciphertextBase64, DEFAULT_KEY_BYTES);
    }

    public static String encrypt(String plaintext, byte[] keyBytes) {
        if (plaintext == null) {
            return null;
        }
        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            SecretKeySpec key = new SecretKeySpec(keyBytes, AES);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decrypt(String ciphertextBase64, byte[] keyBytes) {
        if (ciphertextBase64 == null) {
            return null;
        }
        try {
            byte[] allBytes = Base64.getDecoder().decode(ciphertextBase64);
            ByteBuffer byteBuffer = ByteBuffer.wrap(allBytes);
            byte[] iv = new byte[IV_LENGTH_BYTES];
            byteBuffer.get(iv);
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            SecretKeySpec key = new SecretKeySpec(keyBytes, AES);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
