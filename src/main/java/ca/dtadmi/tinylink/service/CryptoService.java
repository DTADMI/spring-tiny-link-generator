package ca.dtadmi.tinylink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class CryptoService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String ALGO = "MD5";
    private static final String BASE62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String base62EncodeLong(long number) {
        StringBuilder sb = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % 62);
            sb.insert(0, BASE62_CHARACTERS.charAt(remainder));
            number /= 62;
        }

        return sb.toString();
    }

    public long base62DecodeToLong(String base62String) {
        long result = 0;
        for (int i = 0; i < base62String.length(); i++) {
            char c = base62String.charAt(i);
            int value = BASE62_CHARACTERS.indexOf(c);
            result = result * 62 + value;
        }
        return result;
    }

    public String base62EncodeString(String input) {
        StringBuilder sb = new StringBuilder();
        long number = 0;

        for (char c : input.toCharArray()) {
            number = number * 256 + c;
        }

        while (number > 0) {
            int remainder = (int) (number % 62);
            sb.insert(0, BASE62_CHARACTERS.charAt(remainder));
            number /= 62;
        }

        return sb.toString();
    }

    public String base62DecodeToString(String base62String) {
        long number = 0;

        for (char c : base62String.toCharArray()) {
            int value = BASE62_CHARACTERS.indexOf(c);
            number = number * 62 + value;
        }

        StringBuilder sb = new StringBuilder();

        while (number > 0) {
            sb.insert(0, (char) (number % 256));
            number /= 256;
        }

        return sb.toString();
    }

    public String doubleEncryptString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGO);
            byte[] hashBytes = md.digest(input.getBytes());

            // Convert the byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String md5Hash = sb.toString();

            logger.debug("Input: {}", input);
            logger.debug("MD5 Hash: {}", md5Hash);
            return base62EncodeString(md5Hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public String encryptString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGO);
            byte[] hashBytes = md.digest(input.getBytes());

            // Convert the byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String md5Hash = sb.toString();

            logger.debug("Input: {}", input);
            logger.debug("MD5 Hash: {}", md5Hash);
            return base62EncodeString(md5Hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
