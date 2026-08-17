package fr.lacassinauteur.site.identity.domain.port;

import fr.lacassinauteur.site.identity.domain.exception.JetonReinitialisationInvalideException;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeJetonReinitialisationMotDePassePort implements JetonReinitialisationMotDePassePort {

    private final ConcurrentMap<String, JetonDecode> jetons = new ConcurrentHashMap<>();

    @Override
    public String genererJeton(UUID utilisateurId, UUID jetonId) {
        String jeton = "jeton-" + UUID.randomUUID();
        jetons.put(jeton, new JetonDecode(utilisateurId, jetonId));
        return jeton;
    }

    @Override
    public JetonDecode validerEtExtraire(String jeton) {
        JetonDecode decode = jetons.get(jeton);
        if (decode == null) {
            throw new JetonReinitialisationInvalideException("Jeton de réinitialisation invalide ou expiré");
        }
        return decode;
    }
}
