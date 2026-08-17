// reCAPTCHA v3 (cf. ADR-0019) : execute silencieusement au moment de la
// soumission (pas de defi visible), pose le jeton dans le champ cache
// "captchaToken" puis soumet reellement le formulaire. Generique, active par
// attribut plutot que duplique par page.
document.querySelectorAll('form[data-captcha-site-key]').forEach((formulaire) => {
    formulaire.addEventListener('submit', (evenement) => {
        const siteKey = formulaire.dataset.captchaSiteKey;
        if (!siteKey || typeof grecaptcha === 'undefined') {
            return;
        }
        evenement.preventDefault();
        grecaptcha.ready(() => {
            grecaptcha.execute(siteKey, {action: 'submit'}).then((jeton) => {
                formulaire.querySelector('input[name="captchaToken"]').value = jeton;
                formulaire.submit();
            });
        });
    });
});
