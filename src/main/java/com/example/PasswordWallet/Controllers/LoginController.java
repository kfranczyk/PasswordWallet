package com.example.PasswordWallet.Controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
public class LoginController {

    @GetMapping(name = "/login")
    public ModelAndView loginForm(Model model,
                                  @CookieValue(value = "uniqueDeviceID", defaultValue = "") String deviceID,
                                  HttpServletResponse response){

        if(deviceID.equals(""))
            response.addCookie( generateCookie() );

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login.html");
        return modelAndView;
    }

    private Cookie generateCookie() {
        UUID uniqueKey = UUID.randomUUID();
        Cookie cookie = new Cookie("uniqueDeviceID", uniqueKey.toString());

        cookie.setMaxAge(Integer.MAX_VALUE);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

}
