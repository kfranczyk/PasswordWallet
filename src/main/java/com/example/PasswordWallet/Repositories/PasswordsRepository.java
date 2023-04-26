package com.example.PasswordWallet.Repositories;

import com.example.PasswordWallet.Entities.Passwords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PasswordsRepository extends JpaRepository <Passwords, Integer> {
    @Query("select p from Passwords p where p.idpasswords = ?1")
    Passwords findByIdpasswords(Integer idpasswords);
    @Query("""
            select (count(p) > 0) from Passwords p
            where p.passwEncrypted = ?1 and p.passwTitle = ?2 and p.category.title = ?3 and p.category.userCredentials.login = ?4""")
    boolean existsByPasswEncryptedAndPasswTitleAndCategory_TitleAndCategory_UserCredentials_Login(String passwEncrypted, String passwTitle, String title, String login);
    @Transactional
    @Modifying
    @Query("delete from Passwords p")
    int deleteFirstBy();

    @Query("""
            select p from Passwords p
            where p.passwEncrypted = ?1 and p.passwTitle = ?2 and p.category.title = ?3 and p.category.userCredentials.login = ?4""")
    Passwords findByPasswEncryptedAndPasswTitleAndCategory_TitleAndCategory_UserCredentials_Login(String passwEncrypted, String passwTitle, String cattitle, String login);



    //Passwords findByPasswEncryptedAndPasswTitleAndCategory_TitleAndCategory_UserCredentials_Login(String passwEncrypted, String passwTitle, String title, String login);
    @Query("""
            select p from Passwords p
            where p.passwEncrypted = ?1 and p.category.title = ?2 and p.category.userCredentials.login = ?3""")
    Passwords findByPasswEncryptedAndCategory_TitleAndCategory_UserCredentials_Login(String passwEncrypted, String groupTitle, String login);



    @Transactional
    @Modifying
    @Query("update Passwords p set p.passwEncrypted = ?1 where p.idpasswords = ?2")
    void updatePasswEncryptedByIdpasswords(String passwEncrypted, Integer idpasswords);

    List<Passwords> findByCategory_Idpasswordcategory(int idpasswordcategory);





}
