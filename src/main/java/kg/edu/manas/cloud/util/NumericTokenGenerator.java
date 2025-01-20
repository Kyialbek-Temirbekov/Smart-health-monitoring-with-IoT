package kg.edu.manas.cloud.util;

import java.security.SecureRandom;

public class NumericTokenGenerator {
    public static String generateToken(int length) {
        if(length <= 0)
            throw new IllegalArgumentException("Length should be greater than 0");
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder token = new StringBuilder(length);
        for (int i=0;i<length;i++) {
            token.append(secureRandom.nextInt(10));
        }
        return token.toString();
    }

}
