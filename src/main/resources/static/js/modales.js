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

// Confirmation avant soumission (ex. suppression) : attribut plutôt qu'un
// "onsubmit" inline, incompatible avec une Content-Security-Policy stricte
// (cf. ADR sur la CSP).
document.addEventListener('submit', (evenement) => {
    const message = evenement.target.dataset?.confirmer;
    if (message && !confirm(message)) {
        evenement.preventDefault();
    }
});
