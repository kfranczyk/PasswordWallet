package com.example.PasswordWallet.Settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    @Autowired
    AuthenticationProviders authenticationProviders;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManagerBean) throws Exception {

        http.httpBasic().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/",
                        "/login",
                        "/register",
                        "/css/**",
                        "/js/**",
                        "/icons/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .authenticationManager(authenticationManagerBean)
                .formLogin().usernameParameter("login")
                        //.passwordParameter("password")
                        .loginPage("/login").defaultSuccessUrl("/password", true)
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true);

        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        //return Base64.getEncoder();
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManagerBean (HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProviders);
        return authenticationManagerBuilder.build();
    }

    }
