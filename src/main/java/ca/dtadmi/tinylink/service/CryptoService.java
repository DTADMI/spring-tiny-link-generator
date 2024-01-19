package ca.dtadmi.tinylink.service;

import org.springframework.stereotype.Service;

@Service
public class CryptoService {
    private static final String BASE62_CHARACTERS = "ABC01234DEFGHIJKLMNmnopqrstuOPQRSTUV89abcdefghiWXYZ567jklvwxyz";

    public String base62EncodeLong(long number) {
        StringBuilder sb = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % 62);
            sb.insert(0, BASE62_CHARACTERS.charAt(remainder));
            number /= 62;
        }

        return sb.toString();
    }

}
