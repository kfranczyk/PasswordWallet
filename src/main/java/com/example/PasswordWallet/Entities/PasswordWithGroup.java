package com.example.PasswordWallet.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordWithGroup extends Passwords{

    @NotNull
    @Pattern(regexp = "^([\\wąłęźćóżńśĄŁĘŹĆÓŻŃŚ\\-_#\\(\\)]{3,45})$")
    @Size(min = 3, max=45, message = "Group title must be between 3 and 45 characters")
    String categoryName;

    @Override
    public String toString() {
        return super.toString();
    }
}
