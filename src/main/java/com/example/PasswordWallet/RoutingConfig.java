package com.example.PasswordWallet;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
public class RoutingConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("").setViewName("login");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("registerform");
        registry.addViewController("/newEntry").setViewName("newentry");
        registry.addViewController("/password").setViewName("passwords");
        registry.addViewController("/settings").setViewName("settings");
        registry.addViewController("/groups").setViewName("groups");


    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/icons/**",
                "/css/**",
                "/js/**").addResourceLocations(
                    "classpath:/static/icons/",
                    "classpath:/static/css/",
                    "classpath:/static/js/");
    }
}
