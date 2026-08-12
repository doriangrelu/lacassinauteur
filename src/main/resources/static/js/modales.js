// Modales natives (<dialog>) du back-office : ouverture/fermeture par délégation
// d'évènements, sans dépendance JS supplémentaire (cf. ADR-0009).
document.addEventListener('click', (evenement) => {
    const declencheurOuverture = evenement.target.closest('[data-ouvrir-modale]');
    if (declencheurOuverture) {
        const modale = document.getElementById(declencheurOuverture.dataset.ouvrirModale);
        modale?.showModal();
        return;
    }

    const declencheurFermeture = evenement.target.closest('[data-fermer-modale]');
    if (declencheurFermeture) {
        declencheurFermeture.closest('dialog')?.close();
        return;
    }

    if (evenement.target.tagName === 'DIALOG') {
        const rect = evenement.target.getBoundingClientRect();
        const clicDansLaBoite = evenement.clientX >= rect.left && evenement.clientX <= rect.right
            && evenement.clientY >= rect.top && evenement.clientY <= rect.bottom;
        if (!clicDansLaBoite) {
            evenement.target.close();
        }
    }
});
