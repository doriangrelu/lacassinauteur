package fr.lacassinauteur.site.identity.presentation.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class DemanderReinitialisationForm {

    @NotBlank
    @Email
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
