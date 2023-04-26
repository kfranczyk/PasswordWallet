package com.example.PasswordWallet.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
public class EntryBody {

    @NotNull
    String passTitle;
    @NotNull
    String urlPassword;
    @NotNull
    @Pattern(regexp = "^([\\wąłęźćóżńśĄŁĘŹĆÓŻŃŚ\\-_#\\(\\)]{3,45})$")
    @Size(min = 3, max=45, message = "Group title must be between 3 and 45 characters")
    String categoryTitle;

    @Override
    public String toString() {
        return "EntryBody{" +
                "passTitle='" + passTitle + '\'' +
                ", urlPassword='" + urlPassword + '\'' +
                ", categoryTitle='" + categoryTitle + '\'' +
                '}';
    }
}
