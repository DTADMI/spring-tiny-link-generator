package ca.dtadmi.tinylink.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CryptoServiceTest {

    @InjectMocks
    private CryptoService cryptoService;

    @Test
    @DisplayName("Should correctly encode a long number to base62")
    void test_encodeLongToBase62() {
        long number = 1234567890L;
        String expected = "BoU4Rn";
        String result = cryptoService.base62EncodeLong(number);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should return an empty string when encoding 0")
    void test_encodeZeroToBase62() {
        long number = 0L;
        String expected = "";
        String result = cryptoService.base62EncodeLong(number);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle encoding the maximum long value without errors")
    void test_encodeMaxLongToBase62() {
        long number = Long.MAX_VALUE;
        String result = cryptoService.base62EncodeLong(number);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should return an empty string when encoding a negative number")
    void test_encodeNegativeNumberToBase62() {
        long number = -1234567890L;
        String expected = "";
        String result = cryptoService.base62EncodeLong(number);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should handle encoding the minimum long value without errors")
    void test_encodeMinLongToBase62() {
        long number = Long.MIN_VALUE;
        String result = cryptoService.base62EncodeLong(number);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle encoding a number that is one more than the maximum long value")
    void test_encodeMaxLongPlusOneToBase62() {
        long number = Long.MAX_VALUE + 1;
        String result = cryptoService.base62EncodeLong(number);
        assertNotNull(result);
    }
}
