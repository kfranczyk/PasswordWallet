package com.example.PasswordWallet.Settings;

import com.example.PasswordWallet.Repositories.UserRepository;
import com.example.PasswordWallet.EncryptClases.CustomPasswordHMAC;
import com.example.PasswordWallet.EncryptClases.CustomPasswordSHA512Encoder;
import com.example.PasswordWallet.EncryptClases.EncryptFunctions;
import com.example.PasswordWallet.Entities.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthenticationProviders implements AuthenticationProvider {

    UserRepository userRepository;
    CustomPasswordSHA512Encoder sha512Encoder;

    @Autowired
    private HttpServletRequest request;


    public AuthenticationProviders(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.sha512Encoder = new CustomPasswordSHA512Encoder();
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {

        String username = auth.getName();
        String password = String.valueOf(auth.getCredentials());

        UserCredentials userObject = userRepository.findByLogin(username);


        //zabezpieczenia
        //sesja
        RequestContextHolder.currentRequestAttributes().getSessionId();

        String ipAddress = request.getHeader("X-FORWARDED-FOR");

        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        System.out.println("ip:" + ipAddress);



        if(userObject == null)
            throw new BadCredentialsException("External system authentication failed");

        if(userObject.getIsPassHash()==1) {
            String salt = userObject.getPasswordSalt();
            if (userObject.getPassword().equals(sha512Encoder.encode(salt+password))){
                List<GrantedAuthority> grupa = new ArrayList<>();
                grupa.add(new SimpleGrantedAuthority("normalUser"));
                return new UsernamePasswordAuthenticationToken(username, password,grupa);
            }
        }

        String key = Arrays.toString(EncryptFunctions.calculateMD5(username));
        if(userObject.getPassword().equals(CustomPasswordHMAC.encode(password, key) )) {
            List<GrantedAuthority> grupa = new ArrayList<>();
            grupa.add(new SimpleGrantedAuthority("normalUser"));
            return new UsernamePasswordAuthenticationToken(username, password,grupa);
        }

        throw new BadCredentialsException("External system authentication failed");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
