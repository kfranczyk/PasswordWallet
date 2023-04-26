package com.example.PasswordWallet.Entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user_credentials")
@Getter
@Setter
@NoArgsConstructor
public class UserCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iduser", nullable = false)
    public Long idUser;

    @NotNull
    @Column(name = "login")
    private String login;

    @NotNull
    @Column(name = "password")
    private String password;

    @Column(name = "salt")
    private String passwordSalt;

    @NotNull
    @Column(name = "ispasswordahash")
    private int isPassHash;

    @OneToMany(targetEntity = Category.class, mappedBy = "userCredentials", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Category> category = new ArrayList<>();


    public UserCredentials(Long idUser, String login, String password, int isPassHash) {
        this.idUser = idUser;
        this.login = login;
        this.password = password;
        this.isPassHash = isPassHash;
    }

    public UserCredentials(Long idUser, String login, String password, String passwordSalt, int isPassHash) {
        this.idUser = idUser;
        this.login = login;
        this.password = password;
        this.passwordSalt = passwordSalt;
        this.isPassHash = isPassHash;
    }

    //konstruktory
    public UserCredentials(String login, String password, int isPassHash) {
        this.login = login;
        this.password = password;
        this.isPassHash = isPassHash;
    }

    public UserCredentials(String login, String password, String passwordSalt, int isPassHash) {
        this.login = login;
        this.password = password;
        this.passwordSalt = passwordSalt;
        this.isPassHash = isPassHash;
    }


    @Override
    public String toString() {
        return "UserCredentials{" +
                "idUser=" + idUser +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", passwordSalt='" + passwordSalt + '\'' +
                ", isPassHash=" + isPassHash +
                '}';
    }
}
