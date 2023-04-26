package com.example.PasswordWallet.Controllers;

import com.example.PasswordWallet.Entities.Category;
import com.example.PasswordWallet.Entities.UserCredentials;
import com.example.PasswordWallet.Repositories.CategoriesRepository;
import com.example.PasswordWallet.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@RestController
public class GroupController {
    @Autowired
    CategoriesRepository categoriesRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/newGroup")
    public ModelAndView registerGroup (@Valid @ModelAttribute Category tmpGroup, BindingResult binding, Model model){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("groups.html");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (binding.hasErrors()) {
            modelAndView.addObject("errorMessage","Podano nieprawidlowe dane");
            return modelAndView;
        }
        Category category = categoriesRepository.findByTitleAndUserCredentials_Login(tmpGroup.getTitle(), auth.getName());
        if(category != null){
            modelAndView.addObject("errorMessage","Istnieje grupa o takiej nazwie");
            return modelAndView;
        }

        Category newGroup = new Category(tmpGroup.getTitle(), tmpGroup.getColor(), userRepository.findByLogin(auth.getName()));
        categoriesRepository.save(newGroup);

        modelAndView.addObject("errorMessage","Pomyślnie dodano dane");
        return modelAndView;
    }

    @DeleteMapping("/deleteEntry/{groupName}")
    @ResponseBody
    public String deleteGroup(@PathVariable String groupName){
        String returnMessage ="";
        System.out.println("otrzymano do usunięcia:" +groupName);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserCredentials userObj = userRepository.findByLogin(auth.getName());
        try {
            categoriesRepository.deleteByTitleAndUserCredentials(groupName, userObj);
        }catch (Exception e){
            e.printStackTrace();
            returnMessage = "Wystąpił błąd przy usuwaniu:" + groupName;
            return returnMessage;
        }
        returnMessage = "Pomyślnie usunięto:" + groupName;
        return returnMessage;
    }

    @GetMapping("/editGroup/{currGroupName}")
    public ModelAndView editGroup (@Valid @ModelAttribute Category tmpGroup,
                                   BindingResult binding,
                                   @PathVariable("currGroupName") String currGroupName,
                                   Model model){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("groups.html");

        if (binding.hasErrors() || currGroupName==null) {
            modelAndView.addObject("errorMessage","Podano nieprawidlowe dane");
            return modelAndView;
        }

        System.out.println("otrzymano do edycji:" +tmpGroup.getTitle() +" "+ tmpGroup.getColor());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserCredentials userObj = userRepository.findByLogin(auth.getName());

        Boolean isCurrentCategoryExist = categoriesRepository.existsByTitleAndUserCredentials_Login(currGroupName, auth.getName());
        Boolean isAnyCategoryExist = categoriesRepository.existsByTitleAndUserCredentials_Login(tmpGroup.getTitle(), auth.getName());

        //Category category = categoriesRepository.findByTitleAndUserCredentials_Login(currGroupName, auth.getName());
        if(!isCurrentCategoryExist){
            modelAndView.addObject("errorMessage","Nieprawidłowa nazwa grupy do zmiany");
            return modelAndView;
        }
        if(isAnyCategoryExist && ( ! currGroupName.equals(tmpGroup.getTitle()) )){
            modelAndView.addObject("errorMessage","Istnieje już grupa o takiej nazwie");
            return modelAndView;
        }

        categoriesRepository.updateTitleAndColorByTitleAndUserCredentials(tmpGroup.getTitle(), tmpGroup.getColor(),currGroupName,userObj);
        //category.setTitle(tmpGroup.getTitle());
        //category.setColor(tmpGroup.getColor());
        //categoriesRepository.save(category);

        modelAndView.addObject("errorMessage","Pomyślnie wykonano aktualizacje grupy:"+currGroupName);
        return modelAndView;
    }

}
