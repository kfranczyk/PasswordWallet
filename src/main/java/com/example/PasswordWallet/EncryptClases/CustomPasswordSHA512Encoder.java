package com.example.PasswordWallet.EncryptClases;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class CustomPasswordSHA512Encoder  {



    public String encode(CharSequence rawPassword) {
        try {
            //get an instance of SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            String text = rawPassword.toString();
            //calculate message digest of the input string - returns byte array
            byte[] messageDigest = md.digest(text.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean matches(CharSequence rawPassword, String encodedPassword, String salt) {

        String tmpPassw = encode(salt+rawPassword);

        return Objects.equals(encodedPassword, tmpPassw);
    }

}
