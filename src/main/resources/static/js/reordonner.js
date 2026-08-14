// Réordonnancement par glisser-déposer des tableaux back-office (univers,
// collections, livres) : remplace le champ "ordre" numérique manuel. Générique,
// activé par attribut plutôt que dupliqué par page (cf. table[data-formulaire-
// reordonner], qui pointe l'id du <form> à soumettre avec le nouvel ordre).
document.querySelectorAll('table[data-formulaire-reordonner]').forEach((tableau) => {
    const tbody = tableau.querySelector('tbody');
    const formulaire = document.getElementById(tableau.dataset.formulaireReordonner);
    if (!tbody || !formulaire) {
        return;
    }

    let ligneDeplacee = null;

    tbody.querySelectorAll('tr[data-id]').forEach((ligne) => {
        ligne.addEventListener('dragstart', () => {
            ligneDeplacee = ligne;
            ligne.classList.add('opacity-40');
        });

        ligne.addEventListener('dragend', () => {
            ligne.classList.remove('opacity-40');
        });

        ligne.addEventListener('dragover', (evenement) => {
            evenement.preventDefault();
            if (!ligneDeplacee || ligneDeplacee === ligne) {
                return;
            }
            const insererApres = ligne.previousElementSibling === ligneDeplacee;
            tbody.insertBefore(ligneDeplacee, insererApres ? ligne.nextSibling : ligne);
        });

        ligne.addEventListener('drop', (evenement) => {
            evenement.preventDefault();
            formulaire.querySelectorAll('input[name="id"]').forEach((champ) => champ.remove());
            tbody.querySelectorAll('tr[data-id]').forEach((ligneOrdonnee) => {
                const champ = document.createElement('input');
                champ.type = 'hidden';
                champ.name = 'id';
                champ.value = ligneOrdonnee.dataset.id;
                formulaire.appendChild(champ);
            });
            formulaire.submit();
        });
    });
});
