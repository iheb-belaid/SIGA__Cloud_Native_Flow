function addCategory() {
    const input = document.getElementById('newCatName');
    const name  = input.value.trim();
    if (!name) return;

    fetch('/categories', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'name=' + encodeURIComponent(name)
    })
        .then(r => r.json())
        .then(cat => {
            // ajoute l’option dans le select et la sélectionne
            const sel = document.getElementById('categorySelect');
            const opt = document.createElement('option');
            opt.value = cat.id;
            opt.text  = cat.name;
            sel.appendChild(opt);
            sel.value = cat.id;

            // ferme le modal Bootstrap 5
            bootstrap.Modal.getInstance(
                document.getElementById('catModal')
            ).hide();
            input.value = '';
        });
}
