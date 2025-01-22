package kg.edu.manas.cloud.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {
    @Value("  encryption.secretKey")
    private String encodedKey;
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_MODE = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;

    private SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey); // Decode the key
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM); // Create a SecretKey object
    }

    // Generate a random AES key
    public static SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        return keyGenerator.generateKey();
    }

    // Encrypt the data
    public static String encrypt(String data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);  // Generate random IV
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        byte[] encryptedData = cipher.doFinal(data.getBytes());
        // Return Base64 encoded ciphertext along with the IV
        return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encryptedData);
    }

    // Decrypt the data
    public static String decrypt(String encryptedData, SecretKey secretKey) throws Exception {
        String[] parts = encryptedData.split(":");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);

        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] decryptedData = cipher.doFinal(encryptedBytes);
        return new String(decryptedData);
    }
}
