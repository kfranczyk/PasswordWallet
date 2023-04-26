package com.example.PasswordWallet.Repositories;

import com.example.PasswordWallet.Entities.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository <UserCredentials, Integer> {
    @Query("select u from UserCredentials u where u.login = ?1")
    UserCredentials findByLogin(String login);

    @Transactional
    @Modifying
    @Query("update UserCredentials u set u.password = ?1, u.passwordSalt = ?2, u.isPassHash = ?3 where u.idUser = ?4")
    int updatePasswordAndPasswordSaltAndIsPassHashByIdUser(String password, String passwordSalt, int isPassHash, Long idUser);


}
