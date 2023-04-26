package com.example.PasswordWallet.Controllers;

import com.example.PasswordWallet.EncryptClases.CustomPasswordHMAC;
import com.example.PasswordWallet.EncryptClases.CustomPasswordSHA512Encoder;
import com.example.PasswordWallet.EncryptClases.EncryptFunctions;
import com.example.PasswordWallet.Entities.Category;
import com.example.PasswordWallet.Entities.Passwords;
import com.example.PasswordWallet.Entities.UserCredentials;
import com.example.PasswordWallet.Repositories.CategoriesRepository;
import com.example.PasswordWallet.Repositories.PasswordsRepository;
import com.example.PasswordWallet.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Key;
import java.util.*;


@RestController
public class ChangePassword {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordsRepository passwordsRepository;

    private final CustomPasswordSHA512Encoder sha512Encoder = new CustomPasswordSHA512Encoder();

    @Autowired
    RegistrationController registrationController;
    @Autowired
    private CategoriesRepository categoriesRepository;

    @GetMapping("/settings")
    public ModelAndView showSettings(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("Settings.html");
        return modelAndView;
    }


    @PostMapping("/changePassword")
    @ResponseBody
    public ModelAndView changePassword(@RequestParam(name = "currpassword") String currPass,
                               @RequestParam(name = "password") String changePass,
                               @RequestParam(name = "isPassHash") int isHash){

        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserCredentials user = userRepository.findByLogin(auth.getName());

        if(!checkPass(user,currPass)){
            modelAndView.setViewName("Settings.html");
            return modelAndView;
        }

        HashMap <Integer, String> oldPasswordsDecoded = decodeAllPasswords(user);

        System.out.println("dekodowane hasła:"+ oldPasswordsDecoded.toString());

        if(isHash==1)
            updateSHA(user,changePass);
        else if(isHash==0)
            updateHMAC(user,changePass);

        encodeAndPushAllPasswords(oldPasswordsDecoded,auth.getName());

        modelAndView.setViewName("passwords.html");
        return modelAndView;
    }

    private boolean checkPass(UserCredentials user, String pass){
        if(sha512Encoder.matches(pass,user.getPassword(),user.getPasswordSalt()))
            return true;
        else if(CustomPasswordHMAC.matches(pass,user.getPassword(),user.getLogin()))
            return true;
        return false;
    }

    private void updateHMAC(UserCredentials user,String newPass){
        String key = Arrays.toString(EncryptFunctions.calculateMD5(user.getLogin()));
        String tmpPassw = CustomPasswordHMAC.encode(newPass, key);
        if(tmpPassw!=null){
            System.out.println("zaktualizowano HMAC:"+user.getPassword()+"|||"+tmpPassw);

            userRepository.updatePasswordAndPasswordSaltAndIsPassHashByIdUser(
                    tmpPassw,null,0, user.getIdUser()
            );
        }

    }
    private void updateSHA(UserCredentials user, String newPass){
        String salt = EncryptFunctions.generateSalt();
        String tmpPassw =sha512Encoder.encode(salt+newPass);;

        if(tmpPassw!=null){
            System.out.println("zaktualizowano SHA:"+user.getPassword()+"|||"+tmpPassw);

            userRepository.updatePasswordAndPasswordSaltAndIsPassHashByIdUser(
                    tmpPassw,salt,1, user.getIdUser()
            );
        }
    }

    private HashMap <Integer, String> decodeAllPasswords(UserCredentials user){
        Key encryptKey =null;
        HashMap <Integer, String> oldPasswordsDecoded = new HashMap<>();

        try {
            encryptKey = EncryptFunctions.generateKey(user.getPassword());
            List<Passwords> oldPasswords = getAllPasswords(user.getLogin());
            for (Passwords passwordObj : oldPasswords) {
                    String passwordDecrypted = EncryptFunctions.decrypt(passwordObj.getPasswEncrypted(), encryptKey);
                    passwordObj.setPasswEncrypted(passwordDecrypted);
                    oldPasswordsDecoded.put(passwordObj.getIdpasswords(),passwordDecrypted);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return oldPasswordsDecoded;
    }

    public ArrayList<Passwords> getAllPasswords(String userLogin){
        ArrayList<Passwords> passwords = new ArrayList<>();
        ArrayList<Category> categories = categoriesRepository.findByUserCredentials_Login(userLogin);
        for(Category obj : categories){
            passwords.addAll(passwordsRepository.findByCategory_Idpasswordcategory(obj.getIdpasswordcategory()));
        }
        return passwords;
    }

    private void encodeAndPushAllPasswords( HashMap<Integer, String> oldPasswordsDecoded, String username){
        UserCredentials user = userRepository.findByLogin(username);
        Key encryptKey =null;
        String passwEncrypted = null;
        try {
            encryptKey = EncryptFunctions.generateKey(user.getPassword());
            for (Map.Entry<Integer,String> entry : oldPasswordsDecoded.entrySet()) {
                System.out.println("encryptVal="+entry.getValue());
                passwEncrypted = EncryptFunctions.encrypt(entry.getValue(), encryptKey);
                passwordsRepository.updatePasswEncryptedByIdpasswords(passwEncrypted, entry.getKey());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
