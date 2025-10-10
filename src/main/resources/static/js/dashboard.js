const modal = document.getElementById('modal');
const modalTitle = document.getElementById('modal-title');
const modalBody = document.getElementById('modal-body');
const modalForm = document.getElementById('modal-form');

function openModal(type, data = {}) {
    const modal = document.getElementById('modal');
    const modalTitle = document.getElementById('modal-title');
    const modalBody = document.getElementById('modal-body');
    const modalActions = document.getElementById('modal-actions');
    const modalForm = document.getElementById('modal-form');

    modal.style.display = 'flex';
    modalActions.innerHTML = '';

    // Bendras reset
    modalForm.onsubmit = null;
    modalBody.innerHTML = '';

    // --- ADD / EDIT USER ---
    if (type === 'add-user' || type === 'edit-user') {
        modalTitle.textContent = type === 'add-user' ? 'Naujas vartotojas' : 'Redaguoti vartotoją';
        modalBody.innerHTML = `
            <label>Vardas</label><input name="name" value="${data.name || ''}" required>
            <label>El. paštas</label><input name="email" type="email" value="${data.email || ''}" required>
            ${type === 'add-user' ? '<label>Slaptažodis</label><input name="password" type="password" required>' : ''}
            <label>Rolė</label>
            <select name="role">
                <option ${data.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                <option ${data.role === 'MANAGER' ? 'selected' : ''}>MANAGER</option>
                <option ${data.role === 'RESIDENT' ? 'selected' : ''}>RESIDENT</option>
            </select>
            <label>Bendrijos kodas</label>
            <input name="communityCode" required>
        `;
        modalActions.innerHTML = `
            <button type="button" class="cancel-btn" onclick="closeModal()">Atšaukti</button>
            <button type="submit" class="save-btn">Išsaugoti</button>
        `;
        modalForm.onsubmit = (e) =>
            submitForm(e, type === 'add-user' ? '/users' : `/users/${data.id}`, type === 'add-user' ? 'POST' : 'PUT');
    }

    // --- ADD / EDIT COMMUNITY ---
    if (type === 'add-community' || type === 'edit-community') {
        modalTitle.textContent = type === 'add-community' ? 'Nauja bendrija' : 'Redaguoti bendriją';
        modalBody.innerHTML = `
            <label>Kodas</label><input name="code" value="${data.code || ''}" required>
            <label>Pavadinimas</label><input name="name" value="${data.name || ''}" required>
            <label>Adresas</label><input name="address" value="${data.address || ''}" required>
        `;
        modalActions.innerHTML = `
            <button type="button" class="cancel-btn" onclick="closeModal()">Atšaukti</button>
            <button type="submit" class="save-btn">Išsaugoti</button>
        `;
        modalForm.onsubmit = (e) =>
            submitForm(e, type === 'add-community' ? '/communities' : `/communities/${data.id}`, type === 'add-community' ? 'POST' : 'PUT');
    }

    // --- ADD / EDIT FEE ---
    if (type === 'add-fee' || type === 'edit-fee') {
        modalTitle.textContent = type === 'add-fee' ? 'Naujas mokestis' : 'Redaguoti mokestį';
        modalBody.innerHTML = `
            <label>Tipas</label><input name="type" value="${data.type || ''}" required>
            <label>Suma</label><input name="amount" type="number" step="0.01" value="${data.amount || ''}" required>
            <label>Vartotojo ID</label><input name="userId" value="${data.userId || ''}" required>
            <label>Apmokėta</label>
            <select name="paid">
                <option value="true" ${data.paid === 'true' ? 'selected' : ''}>Taip</option>
                <option value="false" ${data.paid === 'false' ? 'selected' : ''}>Ne</option>
            </select>
        `;
        modalActions.innerHTML = `
            <button type="button" class="cancel-btn" onclick="closeModal()">Atšaukti</button>
            <button type="submit" class="save-btn">Išsaugoti</button>
        `;
        modalForm.onsubmit = (e) =>
            submitForm(e, type === 'add-fee' ? '/fees' : `/fees/${data.id}`, type === 'add-fee' ? 'POST' : 'PUT');
    }

    // --- CONFIRM DELETE ---
    if (type === 'confirm-delete') {
        modalTitle.textContent = 'Patvirtinkite trynimą';
        modalBody.innerHTML = `<p>Ar tikrai norite pašalinti šį įrašą?</p>`;
        modalActions.innerHTML = `
            <button type="button" class="cancel-btn" onclick="closeModal()">Atmesti</button>
            <button type="submit" class="confirm-btn">Šalinti</button>
        `;
        modalForm.onsubmit = (e) => submitForm(e, data.url, 'DELETE');
    }
}


function closeModal() {
    modal.style.display = 'none';
}

function submitForm(event, url, method) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const jsonData = Object.fromEntries(formData.entries());

    const token = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    // Išvalom ankstesnes žinutes
    document.querySelectorAll('.modal-error, .modal-success').forEach(el => el.remove());

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json",
            [header]: token
        },
        body: method === "DELETE" ? null : JSON.stringify(jsonData)
    })
        .then(async res => {
            const contentType = res.headers.get("content-type");
            const data = contentType && contentType.includes("application/json")
                ? await res.json().catch(() => ({}))
                : {};

            if (res.ok) {
                let successMsg = "Įrašas sėkmingai išsaugotas.";
                if (method === "POST") successMsg = "Įrašas sėkmingai sukurtas.";
                if (method === "PUT") successMsg = "Įrašas sėkmingai atnaujintas.";
                if (method === "DELETE") successMsg = "Įrašas sėkmingai pašalintas.";

                showModalSuccess(successMsg);
                setTimeout(() => closeModalSmooth(), 500);

                if (method === "DELETE" && isCurrentUserDeleted(url)) {
                    setTimeout(() => {
                        fetch("/logout", {
                            method: "POST",
                            headers: {
                                [header]: token
                            }
                        }).then(() => {
                            window.location.href = "/login?logout";
                        });
                    }, 1000);
                } else {
                    setTimeout(() => location.reload(), 1000);
                }

            } else {
                const errorMsg = data.error || `Klaida: ${res.status}`;
                showModalError(errorMsg);
            }
        })
        .catch(err => showModalError("Tinklo klaida: " + err.message));
}


function closeModalSmooth() {
    const modal = document.querySelector('.modal');
    if (!modal) return;

    setTimeout(() => {
        modal.classList.add('fade-out');
        setTimeout(() => {
            modal.style.display = 'none';
            location.reload();
        }, 500);
    }, 1000);
}

function isCurrentUserDeleted(url) {
    const currentUserEmail = document.body.dataset.userEmail;
    const deletedUserId = url.split("/").pop();
    const currentUserId = document.body.dataset.userId;

    if (currentUserId && deletedUserId === currentUserId) {
       showModalSuccess("Jūsų paskyra pašalinta. Ate...");
        return true;
    }

    return false;
}




function showModalError(message) {
    let alert = document.getElementById('modal-alert');
    if (!alert) {
        alert = document.createElement('div');
        alert.id = 'modal-alert';
        alert.style.marginBottom = '10px';
        document.querySelector('.modal-content').prepend(alert);
    }
    alert.textContent = message;
    alert.style.background = '#fee2e2';
    alert.style.color = '#b91c1c';
    alert.style.padding = '8px';
    alert.style.borderRadius = '4px';
}

function showModalSuccess(message) {
    let alert = document.getElementById('modal-alert');
    if (!alert) {
        alert = document.createElement('div');
        alert.id = 'modal-alert';
        alert.style.marginBottom = '10px';
        document.querySelector('.modal-content').prepend(alert);
    }
    alert.textContent = message;
    alert.style.background = '#dcfce7';
    alert.style.color = '#166534';
    alert.style.padding = '8px';
    alert.style.borderRadius = '4px';
}
window.onclick = function (event) {
    if (event.target === modal) closeModal();
};

function openEditUser(el) {
    openModal('edit-user', {
        id: el.dataset.id,
        name: el.dataset.name,
        email: el.dataset.email,
        role: el.dataset.role
    });
}

function openEditCommunity(el) {
    openModal('edit-community', {
        id: el.dataset.id,
        name: el.dataset.name,
        address: el.dataset.address
    });
}

function openEditFee(el) {
    openModal('edit-fee', {
        id: el.dataset.id,
        type: el.dataset.type,
        amount: el.dataset.amount,
        paid: el.dataset.paid,
        userId: el.dataset.userid
    });
}

