package fr.lacassinauteur.site.legal.application.usecase;

import fr.lacassinauteur.site.legal.application.command.ModifierInformationsLegalesCommand;
import fr.lacassinauteur.site.legal.application.result.InformationsLegalesResult;
import fr.lacassinauteur.site.legal.domain.exception.InformationsLegalesIntrouvablesException;
import fr.lacassinauteur.site.legal.domain.model.InformationsLegales;
import fr.lacassinauteur.site.legal.domain.port.InformationsLegalesRepository;
import org.springframework.stereotype.Component;

@Component
public class ModifierInformationsLegalesUseCase {

    private final InformationsLegalesRepository informationsLegalesRepository;

    public ModifierInformationsLegalesUseCase(InformationsLegalesRepository informationsLegalesRepository) {
        this.informationsLegalesRepository = informationsLegalesRepository;
    }

    public InformationsLegalesResult execute(ModifierInformationsLegalesCommand command) {
        InformationsLegales informations = informationsLegalesRepository.charger()
                .orElseThrow(InformationsLegalesIntrouvablesException::new);

        informations.modifier(command.editeurNom(), command.editeurStatut(), command.editeurAdresse(),
                command.editeurEmail(), command.directeurPublication(), command.hebergeurNom(),
                command.hebergeurAdresse(), command.conservationNewsletterMois(),
                command.conservationContactMois());

        return InformationsLegalesResult.depuis(informationsLegalesRepository.save(informations));
    }
}
