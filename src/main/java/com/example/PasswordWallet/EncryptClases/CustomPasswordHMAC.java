package com.example.PasswordWallet.EncryptClases;

import com.example.PasswordWallet.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import static javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA512;

public class CustomPasswordHMAC {
    @Autowired
    UserRepository userRepository;

    public static String encode(String text, String key){
        Mac sha512Hmac;
        String result="";
        String hashedKey = Arrays.toString(EncryptFunctions.calculateMD5(key));
        try {
            final byte[] byteKey = hashedKey.getBytes(StandardCharsets.UTF_8);
            sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(text.getBytes(StandardCharsets.UTF_8));
            result = Base64.getEncoder().encodeToString(macData);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean matches(CharSequence rawPassword, String encodedPassword, String key) {
        String hashedKey = Arrays.toString(EncryptFunctions.calculateMD5(key));
        String tmpPassw = encode(rawPassword.toString(), hashedKey);
        return encodedPassword.equals(tmpPassw);
    }
}
