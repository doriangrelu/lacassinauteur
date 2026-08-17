package fr.lacassinauteur.site.identity.domain.port;

import fr.lacassinauteur.site.identity.domain.exception.JetonReinitialisationInvalideException;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeJetonReinitialisationMotDePassePort implements JetonReinitialisationMotDePassePort {

    private final ConcurrentMap<String, UUID> jetons = new ConcurrentHashMap<>();

    @Override
    public String genererJeton(UUID utilisateurId) {
        String jeton = "jeton-" + UUID.randomUUID();
        jetons.put(jeton, utilisateurId);
        return jeton;
    }

    @Override
    public UUID validerEtExtraireUtilisateurId(String jeton) {
        UUID utilisateurId = jetons.get(jeton);
        if (utilisateurId == null) {
            throw new JetonReinitialisationInvalideException("Jeton de réinitialisation invalide ou expiré");
        }
        return utilisateurId;
    }
}
