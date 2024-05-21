package gestion.mdp.v2.util;

import gestion.mdp.v2.exception.MyExceptionServerIssue;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16; // 128 bits
    private static final int GCM_IV_LENGTH = 12; // 96 bits

    protected static String encrypt(String input, String key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);

            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

            byte[] encryptedBytes = cipher.doFinal(input.getBytes());

            byte[] encryptedIvAndText = new byte[GCM_IV_LENGTH + encryptedBytes.length];
            System.arraycopy(iv, 0, encryptedIvAndText, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedBytes, 0, encryptedIvAndText, GCM_IV_LENGTH, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(encryptedIvAndText);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new MyExceptionServerIssue("Erreur serveur" + " " + e.getMessage());
        }
    }

    protected static String decrypt(String encryptedInput, String key) {
        try {
            byte[] encryptedIvAndText = Base64.getDecoder().decode(encryptedInput);
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(encryptedIvAndText, 0, iv, 0, GCM_IV_LENGTH);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec);

            byte[] encryptedBytes = new byte[encryptedIvAndText.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedIvAndText, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (NoSuchPaddingException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException |
                 IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            throw new MyExceptionServerIssue("Erreur serveur" + " " + e.getMessage());
        }
    }

    protected static String secretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[16];
        secureRandom.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
