package fr.lacassinauteur.site.biographie.application.command;

/**
 * @param nouvellePhotoContenu contenu binaire de la photo, {@code null} ou vide si
 *                             l'auteur n'en téléverse pas de nouvelle (la photo
 *                             actuelle est alors conservée)
 */
public record ModifierBiographieCommand(String texte, byte[] nouvellePhotoContenu, String nouvellePhotoNomFichier) {
}
