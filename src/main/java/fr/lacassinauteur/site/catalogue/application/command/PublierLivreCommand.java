package fr.lacassinauteur.site.catalogue.application.command;

import java.util.UUID;

public record PublierLivreCommand(UUID livreId, String url, String libelleMarchand) {
}
