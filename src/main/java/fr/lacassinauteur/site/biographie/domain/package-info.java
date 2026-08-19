/**
 * Cœur métier de la présentation publique de l'auteur (page « Auteur » du brief
 * §5) : photo et texte de présentation, éditables par l'auteur lui-même. Aucune
 * dépendance à un framework.
 *
 * Domaine nommé « biographie » et non « auteur » pour éviter toute ambiguïté avec
 * le rôle {@code AUTEUR} du domaine {@code identity} — cf. ADR-0028.
 * Voir docs/architecture/domain-model.md.
 */
package fr.lacassinauteur.site.biographie.domain;
