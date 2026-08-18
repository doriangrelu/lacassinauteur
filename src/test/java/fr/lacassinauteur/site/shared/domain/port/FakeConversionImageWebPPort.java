package fr.lacassinauteur.site.shared.domain.port;

import fr.lacassinauteur.site.shared.domain.exception.ConversionImageEchoueeException;

import java.nio.charset.StandardCharsets;

public class FakeConversionImageWebPPort implements ConversionImageWebPPort {

    private boolean echoue = false;

    public void simulerEchec() {
        this.echoue = true;
    }

    @Override
    public byte[] convertirEnWebp(byte[] contenuOriginal) {
        if (echoue) {
            throw new ConversionImageEchoueeException("Échec simulé");
        }
        return "webp-simule".getBytes(StandardCharsets.UTF_8);
    }
}
