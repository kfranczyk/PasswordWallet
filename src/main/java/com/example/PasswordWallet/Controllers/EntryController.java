package com.example.PasswordWallet.Controllers;

import com.example.PasswordWallet.EncryptClases.EncryptFunctions;
import com.example.PasswordWallet.Entities.*;
import com.example.PasswordWallet.Repositories.CategoriesRepository;
import com.example.PasswordWallet.Repositories.PasswordsRepository;
import com.example.PasswordWallet.Repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Key;
import java.util.ArrayList;

@RestController
public class EntryController {

    UserRepository userRepository;
    PasswordsRepository passwordsRepository;
    private final CategoriesRepository categoriesRepository;

    public EntryController(UserRepository userRepository, PasswordsRepository passwordsRepository,
                           CategoriesRepository categoriesRepository) {
        this.userRepository = userRepository;
        this.passwordsRepository = passwordsRepository;
        this.categoriesRepository = categoriesRepository;
    }


    @PostMapping("/newEntryRegister")
    public ModelAndView addEntry(@Valid @ModelAttribute PasswordWithGroup tmpPassword,BindingResult bindingResult, Model model){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("otrzymano do wstawienia :"+ tmpPassword + " " + tmpPassword.getCategoryName() + " " +auth.getName());


        if (bindingResult.hasErrors()) {
            modelAndView.addObject("errorMessage","Podano nieprawidlowe dane");
            return modelAndView;
        }



        UserCredentials userObj = userRepository.findByLogin(auth.getName());
        Category passwCategoryObj = categoriesRepository.findByUserCredentials_IdUserAndTitle(userObj.idUser, tmpPassword.getCategoryName());

        System.out.println("podano dane"+ tmpPassword.toString());

        try {
            Key key = EncryptFunctions.generateKey(userObj.getPassword());
            String passwEncrypted = EncryptFunctions.encrypt(tmpPassword.getPasswEncrypted(), key);
            String site = tmpPassword.getPasswWebAdd()==null? "": tmpPassword.getPasswWebAdd();
            String description = tmpPassword.getPasswDescription()==null? "": tmpPassword.getPasswDescription();


            Passwords DBpassword = passwordsRepository.findByPasswEncryptedAndPasswTitleAndCategory_TitleAndCategory_UserCredentials_Login(
                    passwEncrypted,
                    tmpPassword.getPasswTitle(),
                    tmpPassword.getCategoryName(),
                    auth.getName());

            System.out.println("wartość sprawdzarki:"+ DBpassword);
            if(DBpassword != null){
                modelAndView.setViewName("newentry.html");
                modelAndView.addObject("errorMessage","istnieje już takie samo takie hasło");
                return modelAndView;
            }

            Passwords newEntry = new Passwords(
                    passwEncrypted,
                    tmpPassword.getPasswTitle(),
                    site,
                    description,
                    passwCategoryObj);

            System.out.println("stworzono hasło:"+newEntry.toString());
            passwordsRepository.save(newEntry);
            //userObj.setUserPasswords((List<Passwords>) passwordsRepository.findByUserCredentials_IdUser(userObj.getidUser()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        modelAndView.setViewName("passwords.html");
        return modelAndView;
    }

    @GetMapping("/newEntry")
    public ModelAndView showForm(Model model, HttpServletRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        ArrayList<Category> categories = categoriesRepository.findByUserCredentials_Login(auth.getName());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("categories", categories);
        modelAndView.setViewName("newentry.html");
        return modelAndView;
    }


    @PostMapping("/getEntries")
    public String getUserEntries(Model model){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //List<Passwords> passwordsList = getAllPasswords(  auth.getName() );

        ArrayList<Category> categories = categoriesRepository.findByUserCredentials_Login(auth.getName());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String arrayToJson = "";
        try {
            //arrayToJson = objectMapper.writeValueAsString(passwordsList);
            arrayToJson = objectMapper.writeValueAsString(categories);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return arrayToJson;
    }

    public ArrayList<Passwords> getAllPasswords(String userLogin){
        ArrayList<Passwords> passwords = new ArrayList<>();
        ArrayList<Category> categories = categoriesRepository.findByUserCredentials_Login(userLogin);
        for(Category obj : categories){
            passwords.addAll(passwordsRepository.findByCategory_Idpasswordcategory(obj.getIdpasswordcategory()));
        }
        return passwords;
    }


    @PostMapping("/getEntry/")
    @ResponseBody
    public String decodePassw(@RequestBody EntryBody entryBody, BindingResult bindingResult){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserCredentials user = userRepository.findByLogin(auth.getName());
        //Passwords passwordEncrypted = passwordsRepository.findByPasswEncryptedAndCategory_TitleAndCategory_UserCredentials_Login(entryBody.getUrlPassword(), entryBody.getCategoryTitle(), auth.getName());
        Passwords passwordEncrypted = passwordsRepository.findByPasswEncryptedAndPasswTitleAndCategory_TitleAndCategory_UserCredentials_Login(
                entryBody.getUrlPassword(),
                entryBody.getPassTitle(),
                entryBody.getCategoryTitle(),
                auth.getName());

        System.out.println("otrzymano tytuł: "+entryBody.getPassTitle());
        System.out.println("otrzymano grupe: "+entryBody.getCategoryTitle());
        System.out.println("otrzymano haslo: "+entryBody.getUrlPassword());
        String retVal ="{";

        Key key = null;
        try {
            key = EncryptFunctions.generateKey(user.getPassword());
            retVal += "\"decodedPass\":\"" + EncryptFunctions.decrypt(passwordEncrypted.getPasswEncrypted(), key) + "\"";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        retVal +="}";

        return retVal;
    }

    @DeleteMapping("/deletePassword")
    @ResponseBody
    public String deleteGroup(@RequestBody EntryBody entryBody, BindingResult bindingResult){
        String returnMessage ="";
        if(bindingResult.hasErrors()){
            returnMessage = "Wysłano nieprawidłowe dane dla hasła:" + entryBody.getPassTitle();
            return returnMessage;
        }

        System.out.println("otrzymano do usunięcia:" + entryBody);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserCredentials userObj = userRepository.findByLogin(auth.getName());

        try {
            Passwords password = passwordsRepository.findByPasswEncryptedAndPasswTitleAndCategory_TitleAndCategory_UserCredentials_Login(
                        entryBody.getUrlPassword(),
                        entryBody.getPassTitle(),
                        entryBody.getCategoryTitle(),
                        auth.getName());

            System.out.println("chce usunać id:"+password.getIdpasswords());
            passwordsRepository.deleteById(password.getIdpasswords());
            //passwordsRepository.delete(password);
        }catch (Exception e){
            e.printStackTrace();
            returnMessage = "Wystąpił błąd przy usuwaniu:" + entryBody.getPassTitle();
            return returnMessage;
        }
        returnMessage = "Pomyślnie usunięto:" + entryBody.getPassTitle();
        return returnMessage;
    }


    @PostMapping("/editStoredPasswordForm")
    public ModelAndView showEditStoredPasswordForm(@ModelAttribute PasswordWithGroup password,
                                                   BindingResult bindingResult){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("otrzymano: "+password + " " + password.getCategoryName());
        ModelAndView modelAndView = new ModelAndView();
        if(bindingResult.hasErrors()){
            modelAndView.addObject("errorMessage","Podano nieprawidlowe dane");
            modelAndView.setViewName("passwords.html");
            return modelAndView;
        }


        Passwords dbPassword = passwordsRepository.findByPasswEncryptedAndPasswTitleAndCategory_TitleAndCategory_UserCredentials_Login(
                password.getPasswEncrypted(),
                password.getPasswTitle(),
                password.getCategoryName(),
                auth.getName()
        );
        if(dbPassword == null){
            modelAndView.addObject("errorMessage","Podane hasło nie istnieje");
            modelAndView.setViewName("passwords.html");
            return modelAndView;
        }

        UserCredentials user = userRepository.findByLogin(auth.getName());
        Key key = null;
        try {
            key = EncryptFunctions.generateKey(user.getPassword());
            password.setPasswEncrypted(EncryptFunctions.decrypt(password.getPasswEncrypted(), key));



        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        ArrayList<Category> categories = categoriesRepository.findByUserCredentials_Login(auth.getName());
        modelAndView.addObject("categories", categories);
        modelAndView.addObject("idPassw",dbPassword.getIdpasswords());

        modelAndView.addObject("passwObj", password);
        modelAndView.setViewName("editPassword.html");
        return modelAndView;
    }

        @PostMapping("/editStoredPassword")
        public ModelAndView editStoredPassword(@Valid @ModelAttribute PasswordWithGroup password,
                                                   BindingResult bindingResult){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(bindingResult.hasErrors()){
            ArrayList<Category> categories = categoriesRepository.findByUserCredentials_Login(auth.getName());
            modelAndView.addObject("categories", categories);
            modelAndView.addObject("errorMessage","Podano nieprawidlowe dane");
            modelAndView.addObject("passwObj", password);
            modelAndView.addObject("idPassw",password.getIdpasswords());
            modelAndView.setViewName("editPassword.html");
            return modelAndView;
        }
        Integer idpasswInt = password.getIdpasswords();
        //boolean isPasswExist = passwordsRepository.existsById(idpasswInt);
        Passwords passwords = passwordsRepository.findByIdpasswords(idpasswInt);
        if (passwords == null){
            ArrayList<Category> categories = categoriesRepository.findByUserCredentials_Login(auth.getName());
            modelAndView.addObject("categories", categories);
            modelAndView.addObject("errorMessage","Nie istnieje dane hasło");
            modelAndView.addObject("passwObj", password);
            modelAndView.addObject("idPassw",password.getIdpasswords());
            modelAndView.setViewName("editPassword.html");
            return modelAndView;
        }
        password.setIdpasswords(passwords.getIdpasswords());

        UserCredentials userObj = userRepository.findByLogin(auth.getName());
        try {
            Key key = EncryptFunctions.generateKey(userObj.getPassword());
            String passwEncrypted = EncryptFunctions.encrypt(password.getPasswEncrypted(), key);

            Passwords DBpassword = passwordsRepository.findByPasswEncryptedAndPasswTitleAndCategory_TitleAndCategory_UserCredentials_Login(
                    passwEncrypted,
                    password.getPasswTitle(),
                    password.getCategoryName(),
                    auth.getName());

            if(DBpassword != null){
                ArrayList<Category> categories = categoriesRepository.findByUserCredentials_Login(auth.getName());
                modelAndView.addObject("categories", categories);
                modelAndView.addObject("errorMessage","Nie istnieje dane hasło");
                modelAndView.addObject("passwObj", password);
                modelAndView.addObject("idPassw",password.getIdpasswords());
                modelAndView.setViewName("editPassword.html");
                modelAndView.addObject("errorMessage","istnieje już takie samo takie hasło");
                return modelAndView;
            }



            passwords.setPasswEncrypted(passwEncrypted);
            passwords.setPasswTitle(password.getPasswTitle());
            passwords.setPasswWebAdd(password.getPasswWebAdd());
            passwords.setPasswDescription(password.getPasswDescription());
            passwordsRepository.save(passwords);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        modelAndView.addObject("errorMessage","Poprawnie zaktualizowano dane");
        modelAndView.setViewName("passwords.html");
        return modelAndView;
    }
}
