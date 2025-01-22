package kg.edu.manas.cloud.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class KeyGenerationUtil {

    private static final String ALGORITHM = "AES";  // You can use any supported algorithm (e.g., AES, DES, etc.)
    private static final int KEY_SIZE = 256;  // AES 256-bit key size (you can use 128 or 192 as well)

    public static void main(String[] args) throws Exception {
        // Generate the secret key using KeyGenerator
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE);  // Initialize with the desired key size (e.g., 256 bits for AES)
        SecretKey secretKey = keyGenerator.generateKey();

        // Convert the key to a byte array
        byte[] keyBytes = secretKey.getEncoded();

        // Base64 encode the byte array
        String encodedKey = Base64.getEncoder().encodeToString(keyBytes);

        // Print the base64-encoded key (this is what you can store in your application.yml or environment variable)
        System.out.println("Base64 Encoded Key: " + encodedKey);
    }
}

