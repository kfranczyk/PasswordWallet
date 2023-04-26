package com.example.PasswordWallet.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "password_category")
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpasswordcategory")
    private int idpasswordcategory;

    @NotNull
    @Pattern(regexp = "^([\\wąłęźćóżńśĄŁĘŹĆÓŻŃŚ\\-_#\\(\\)]{3,45})$")
    @Size(min = 3, max=45, message = "Group title must be between 3 and 45 characters")
    @Column(name = "title")
    private String title;

    @NotNull
    @Size(min = 4, max = 7, message = "Color accepts Hex values with length 4-7 (for example #FF00FF")
    @Column(name = "color")
    private String color;

    @OneToMany(targetEntity = Passwords.class, mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Passwords> passwords = new ArrayList<>();


    @JsonIgnore
    @ManyToOne(targetEntity = UserCredentials.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_credentials_iduser", referencedColumnName = "idUser",nullable = false)
    private UserCredentials userCredentials;

    public Category(String title, String color, UserCredentials userCredentials) {
        this.title = title;
        this.color = color;
        this.userCredentials = userCredentials;
    }


}
