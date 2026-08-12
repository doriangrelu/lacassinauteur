package fr.lacassinauteur.site.catalogue.presentation.form;

import jakarta.validation.constraints.NotBlank;

public class PublierLivreForm {

    @NotBlank
    private String url;

    private String libelleMarchand;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLibelleMarchand() {
        return libelleMarchand;
    }

    public void setLibelleMarchand(String libelleMarchand) {
        this.libelleMarchand = libelleMarchand;
    }
}
