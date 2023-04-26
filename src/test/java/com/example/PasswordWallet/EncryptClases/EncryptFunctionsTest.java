package com.example.PasswordWallet.EncryptClases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.testng.Assert;

import java.security.Key;
import java.util.stream.Stream;


class EncryptFunctionsTest {

    @ParameterizedTest
    @CsvSource({"adad,TEST", "tEst, XXAA", "Java,JAVA"})
    void encryptDecrypt_true_decodedEqualsInput(String masterPassword, String passwordToEncode) {
        try {
            Key key = EncryptFunctions.generateKey(masterPassword);
            String passEncoded = EncryptFunctions.encrypt(passwordToEncode,key);
            String passDecoded = EncryptFunctions.decrypt(passEncoded,key);
            Assert.assertEquals(passwordToEncode,passDecoded);
            System.out.println(passDecoded);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @NullSource
    void encryptDecrypt_throwNullPointerExcept_decodedEqualsInput(String masterPassword) {
        try {
            EncryptFunctions.generateKey(masterPassword);

        } catch (NullPointerException e){
          return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @ParameterizedTest
    @ValueSource(strings = {"asdfasdf", "bbbb", "11111","\n"})
    void calculateMD5_true_checkLength(String input){
        byte[] funReturn = EncryptFunctions.calculateMD5(input);
        Assert.assertEquals(funReturn.length, 16);
    }


    @ParameterizedTest
    @MethodSource("saltLenBoundariesProvider")
    void generateSalt_valueInRange_checkLengthOfSalt(int min, int max) {
        Boolean testResult = false;
        for(int i=0;i<30;i++) {
            String funResult = EncryptFunctions.generateSalt();
            if (funResult.length() >= min && funResult.length() <= max)
                testResult = true;
        }
        Assert.assertTrue(testResult);
    }

    private static Stream<Arguments> saltLenBoundariesProvider() {
        return Stream.of(
                Arguments.of(10, 30),
                Arguments.of(5, 30)
        );
    }

    @ParameterizedTest
    @MethodSource("saltLenBoundariesForFailProvider")
    void generateSalt_false_checkLengthOfSalt(int min, int max) {
        Boolean testResult = true;
        for(int i=0;i<30;i++) {
            String funResult = EncryptFunctions.generateSalt();
            if (funResult.length() < min || funResult.length() > max)
                testResult = false;
        }

        Assert.assertTrue(!testResult);
    }

    private static Stream<Arguments> saltLenBoundariesForFailProvider() {
        return Stream.of(
                Arguments.of(10, 20),
                Arguments.of(15, 30),
                Arguments.of(20, 40),
                Arguments.of(25, 30)
        );
    }

}