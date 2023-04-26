package com.example.PasswordWallet.Repositories;

import com.example.PasswordWallet.Entities.Category;
import com.example.PasswordWallet.Entities.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Transactional
public interface CategoriesRepository extends JpaRepository<Category,Integer> {
    @Modifying
    @Query("update Category c set c.title = ?1, c.color = ?2 where c.title = ?3 and c.userCredentials = ?4")
    int updateTitleAndColorByTitleAndUserCredentials(String title, String color, String currTitle, UserCredentials userCredentials);
    ArrayList<Category> findByUserCredentials_IdUser(Long idUser);

    ArrayList<Category> findByUserCredentials_Login(String login);

    Category findByUserCredentials_IdUserAndTitle(Long idUser, String title);

    @Modifying
    @Query("delete from Category c where c.title = ?1 and c.userCredentials = ?2")
    int deleteByTitleAndUserCredentials(String title, UserCredentials userCredentials);

    @Query("select (count(c) > 0) from Category c where c.title = ?1 and c.userCredentials.login = ?2")
    boolean existsByTitleAndUserCredentials_Login(String title, String login);

    //long deleteByTitleAndUserCredentials(String title, UserCredentials userCredentials);

    //long deleteByIdpasswordcategory(int idpasswordcategory);


    @Nullable
    Category findByTitleAndUserCredentials_Login(String title, String login);


}
