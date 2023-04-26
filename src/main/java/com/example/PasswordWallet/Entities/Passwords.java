package com.example.PasswordWallet.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "passwords")
@Getter
@Setter
@NoArgsConstructor
public class Passwords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpasswords")
    private Integer idpasswords;

    @NotNull
    @Column(name = "password")
    private String passwEncrypted;

    @NotNull
    @Column(name = "title")
    @Size(min = 3, max=45, message = "Password title must be between 3 and 45 characters")
    private String passwTitle;

    @Column(name = "webaddress")
    @Size(min = 0, max=256, message = "Password site must be between 0 and 256 characters")
    private String passwWebAdd;

    @Column(name = "description")
    @Size(min = 0, max=256, message = "Password site must be between 0 and 256 characters")
    private String passwDescription;


    @JsonIgnore
    @ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "idpasswordcategory", referencedColumnName = "idpasswordcategory",nullable = false)
    private Category category;


    public Passwords(String passwEncrypted, String passwTitle, Category category) {
        this.passwEncrypted = passwEncrypted;
        this.passwTitle = passwTitle;
        this.category = category;
    }


    public Passwords(String passwEncrypted, String passwTitle, String passwWebAdd, Category category) {
        this.passwEncrypted = passwEncrypted;
        this.passwTitle = passwTitle;
        this.passwWebAdd = passwWebAdd;
        this.category = category;
    }

    public Passwords(String passwEncrypted, String passwTitle, String passwWebAdd, String passwDescription, Category category) {
        this.passwEncrypted = passwEncrypted;
        this.passwTitle = passwTitle;
        this.passwWebAdd = passwWebAdd;
        this.passwDescription = passwDescription;
        this.category = category;
    }


    @Override
    public String toString() {
        return "Passwords{" +
                "idPassword=" + idpasswords +
                ", passwEncrypted='" + passwEncrypted + '\'' +
                ", passwTitle='" + passwTitle + '\'' +
                ", passwWebAdd='" + passwWebAdd + '\'' +
                ", passwDescription='" + passwDescription + '\'' +
                '}';
    }
}
