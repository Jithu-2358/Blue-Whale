// service/CryptoService.java
package com.bluewhale.service;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    // MUST match the key embedded in the Android APK (base64 of 32 bytes)
    private static final String AES_KEY_B64 = "bW9udGVyZXlhcGFzc3dvcmQxMjM0NTY3OA==";

    public String encrypt(String plaintext) {
        try {
            byte[] key = Base64.getDecoder().decode(AES_KEY_B64);
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            byte[] enc = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[16 + enc.length];
            System.arraycopy(iv, 0, combined, 0, 16);
            System.arraycopy(enc, 0, combined, 16, enc.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedB64) {
        try {
            byte[] key = Base64.getDecoder().decode(AES_KEY_B64);
            byte[] combined = Base64.getDecoder().decode(encryptedB64);
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, 16);
            byte[] ciphertext = new byte[combined.length - 16];
            System.arraycopy(combined, 16, ciphertext, 0, ciphertext.length);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}