/**
 * Cœur métier des informations légales du site : identité de l'éditeur, de
 * l'hébergeur et paramètres RGPD, injectés dans les pages « Mentions légales » et
 * « Politique de confidentialité ». Aucune dépendance à un framework.
 *
 * Seules ces <em>variables</em> sont éditables : le texte légal lui-même vit dans
 * les gabarits Thymeleaf, car il n'a pas vocation à changer au quotidien
 * — cf. ADR-0029. Voir docs/architecture/domain-model.md.
 */
package fr.lacassinauteur.site.legal.domain;
