package com.example.PasswordWallet.Controllers;

import com.example.PasswordWallet.EncryptClases.CustomPasswordHMAC;
import com.example.PasswordWallet.EncryptClases.CustomPasswordSHA512Encoder;
import com.example.PasswordWallet.EncryptClases.EncryptFunctions;
import com.example.PasswordWallet.Entities.Category;
import com.example.PasswordWallet.Entities.UserCredentials;
import com.example.PasswordWallet.Repositories.CategoriesRepository;
import com.example.PasswordWallet.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@RestController
public class RegistrationController {


    UserRepository userRepository;
    private CustomPasswordSHA512Encoder sha512Encoder = new CustomPasswordSHA512Encoder();
    private final CategoriesRepository categoriesRepository;


    @Autowired
    public RegistrationController(UserRepository userRepository,
                                  CategoriesRepository categoriesRepository) {
        this.userRepository = userRepository;
        this.categoriesRepository = categoriesRepository;
    }

    @GetMapping("/register")
    public ModelAndView registerModel (Model model){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("registerform.html");
        return modelAndView;
    }


    @PostMapping("/register")
    public ModelAndView registerUser (@ModelAttribute UserCredentials tmpUser, Model model){

        if(tmpUser.getIsPassHash()==1)
            registerSHA(tmpUser);
        else if(tmpUser.getIsPassHash()==0)
            registerHMAC(tmpUser);

        createDefCat(tmpUser.getLogin());


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login.html");
        return modelAndView;
    }

    private void createDefCat(String login){
        UserCredentials user = userRepository.findByLogin(login);
        Category defaultCategory = new Category("default","#217CF2",user);
        ArrayList<Category> tmp = new ArrayList<>();
        tmp.add(defaultCategory);

        user.setCategory( tmp);
        categoriesRepository.save(defaultCategory);
    }

    public void registerSHA(UserCredentials tmpUser){
        String salt = EncryptFunctions.generateSalt();
        String tmpPassw;
        UserCredentials newUser;

        tmpPassw = sha512Encoder.encode(salt+tmpUser.getPassword());

        if(tmpPassw!=null){
            newUser = new UserCredentials(tmpUser.getLogin(),tmpPassw,salt,tmpUser.getIsPassHash());

            System.out.println("stworzono SHA512:"+newUser.toString());
            userRepository.save(newUser);
        }
    }

    public void registerHMAC(UserCredentials tmpUser){
        String tmpPassw;
        UserCredentials newUser;
        tmpPassw = CustomPasswordHMAC.encode(tmpUser.getPassword(), tmpUser.getLogin());

        if(tmpPassw!=null){
            newUser = new UserCredentials(tmpUser.getLogin(),tmpPassw,tmpUser.getIsPassHash());

            System.out.println("stworzono HMAC:"+newUser.toString());
            userRepository.save(newUser);
        }
    }


}
