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

    // Gaunam naudotojo rolę iš <body> (ją perduosime iš Thymeleaf)
    const currentRole = document.body.dataset.role || 'ADMIN';
    const currentCommunityCode = document.body.dataset.communityCode || '';

    // Formuojam laukus
    let communityField = '';
    if (currentRole === 'ADMIN') {
        communityField = `
            <label>Bendrijos kodas</label>
            <input name="communityCode" value="${data.communityCode || ''}" required>
        `;
    } else if (currentRole === 'MANAGER') {
        communityField = `
            <input type="hidden" name="communityCode" value="${currentCommunityCode}">
        `;
    }

    modalBody.innerHTML = `
        <label>Vardas</label><input name="name" value="${data.name || ''}" required>
        <label>El. paštas</label><input name="email" type="email" value="${data.email || ''}" required>
        ${type === 'add-user' ? '<label>Slaptažodis</label><input name="password" type="password">' : ''}
        <label>Rolė</label>
        <select name="role">
            <option ${data.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
            <option ${data.role === 'MANAGER' ? 'selected' : ''}>MANAGER</option>
            <option ${data.role === 'RESIDENT' ? 'selected' : ''}>RESIDENT</option>
        </select>
        ${communityField}
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

    // --- ADD / EDIT FEE (Paslauga) ---
    if (type === 'add-fee' || type === 'edit-fee') {
        modalTitle.textContent = type === 'add-fee' ? 'Nauja paslauga' : 'Redaguoti paslaugą';


        modalBody.innerHTML = `
        <label>Pavadinimas</label>
        <input name="name" value="${data.name || ''}" required>

        <label>Matavimo vienetas</label>
        <input name="unit" value="${data.unit || ''}" required>

        <label>Aprašymas</label>
        <textarea name="description" rows="3" style="width:100%">${data.description || ''}</textarea>
       
    `;

        modalActions.innerHTML = `
        <button type="button" class="cancel-btn" onclick="closeModal()">Atšaukti</button>
        <button type="submit" class="save-btn">Išsaugoti</button>
    `;

        modalForm.onsubmit = (e) =>
            submitForm(e, type === 'add-fee' ? '/fees' : `/fees/${data.id}`, type === 'add-fee' ? 'POST' : 'PUT');
    }

    // --- ADD / EDIT PRICE ---
    if (type === 'add-price' || type === 'edit-price') {
        modalTitle.textContent = type === 'add-price' ? 'Nauja kaina' : 'Redaguoti kainą';

        const currentRole = document.body.dataset.role;
        const currentCommunityId = document.body.dataset.communityId;

        // --- Bendrijos laukelis ---
        let communityField = '';
        if (currentRole === 'ADMIN') {
            communityField = `
            <label>Bendrija</label>
            <select name="communityId" required id="communitySelect">
                <option value="">Įkeliama...</option>
            </select>
        `;
        } else if (currentRole === 'MANAGER') {
            communityField = `
            <input type="hidden" name="communityId" value="${currentCommunityId}">
        `;
        }

        // --- Modal turinys ---
        modalBody.innerHTML = `
        <label>Paslauga</label>
        <select name="feeId" required id="feeSelect">
            <option value="">Įkeliama...</option>
        </select>

        ${communityField}

        <label>Kaina (€)</label>
        <input name="amount" type="number" step="0.01" value="${data.amount || ''}" required>

        <label>Galioja nuo</label>
        <input name="validFrom" type="date" value="${data.validFrom || ''}" required>

        <label>Galioja iki</label>
        <input name="validTo" type="date" value="${data.validTo || ''}">
    `;

        modalActions.innerHTML = `
        <button type="button" class="cancel-btn" onclick="closeModal()">Atšaukti</button>
        <button type="submit" class="save-btn">Išsaugoti</button>
    `;

        // --- Paslaugos ---
        console.log("📡 Kraunamos paslaugos...");
        fetch('/api/fees')
            .then(res => {
                console.log("📬 /fees status:", res.status);
                return res.json();
            })
            .then(fees => {
                console.table(fees);
                const feeSelect = document.getElementById('feeSelect');
                if (!feeSelect) return console.error("⚠️ feeSelect nerastas modale!");
                if (fees.length === 0) {
                    feeSelect.innerHTML = `<option disabled>Nėra paslaugų</option>`;
                } else {
                    feeSelect.innerHTML = fees.map(f => `<option value="${f.id}">${f.name}</option>`).join('');
                }
            })
            .catch(err => console.error("❌ Klaida kraunant paslaugas:", err));

        // --- Bendrijos tik ADMIN ---
        if (currentRole === 'ADMIN') {
            console.log("📡 Kraunamos bendrijos adminui...");
            fetch('/api/communities')
                .then(res => {
                    console.log("📬 /communities status:", res.status);
                    if (!res.ok) throw new Error("HTTP klaida: " + res.status);
                    return res.json();
                })
                .then(communities => {
                    console.table(communities);
                    const communitySelect = document.getElementById('communitySelect');
                    if (!communitySelect) return console.error("⚠️ communitySelect nerastas modale!");
                    if (communities.length === 0) {
                        communitySelect.innerHTML = `<option disabled>Nėra bendrijų</option>`;
                    } else {
                        communitySelect.innerHTML = communities
                            .map(c => `<option value="${c.id}">${c.name}</option>`)
                            .join('');
                    }
                })
                .catch(err => {
                    console.error("❌ Klaida kraunant bendrijas:", err);
                    const communitySelect = document.getElementById('communitySelect');
                    if (communitySelect)
                        communitySelect.innerHTML = `<option disabled>Klaida kraunant duomenis</option>`;
                });
        }

        // --- Submit ---
        modalForm.onsubmit = (e) =>
            submitForm(e, type === 'add-price' ? '/prices' : `/prices/${data.id}`, type === 'add-price' ? 'POST' : 'PUT');
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
                let successMsg = "Įrašas sėkmingai išsaugotas [from JS].";
                if (method === "POST") successMsg = "Įrašas sėkmingai sukurtas. [from JS]";
                if (method === "PUT") successMsg = "Įrašas sėkmingai atnaujintas. [from JS]";
                if (method === "DELETE") successMsg = "Įrašas sėkmingai pašalintas. [from JS]";

                showModalMessage(successMsg, "success");
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
                showModalMessage(errorMsg, "error");
            }
        })
        .catch(err => showModalMessage("Tinklo klaida: " + err.message, "error"));

}


function closeModalSmooth() {
    const modal = document.querySelector('.modal');
    if (!modal) return;

    setTimeout(() => {
        modal.classList.add('fade-out');
        setTimeout(() => {
            modal.style.display = 'none';
            location.reload();
        }, 1500);
    }, 3000);
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
        code: el.dataset.code,          // ← pridėta ši eilutė
        name: el.dataset.name,
        address: el.dataset.address
    });
}


function openEditFee(el) {
    openModal('edit-fee', {
        id: el.dataset.id,
        name: el.dataset.name,
        unit: el.dataset.unit,
        description: el.dataset.description
    });
}

function openEditPrice(el) {
    openModal('edit-price', {
        id: el.dataset.id,
        feeId: el.dataset.feeid,
        communityId: el.dataset.communityid,
        amount: el.dataset.amount,
        validFrom: el.dataset.validfrom,
        validTo: el.dataset.validto
    });
}

function showModalMessage(message, type = "success") {
    let alertBox = document.getElementById("modal-alert");
    if (!alertBox) {
        alertBox = document.createElement("div");
        alertBox.id = "modal-alert";
        alertBox.style.marginBottom = "10px";
        alertBox.style.padding = "10px";
        alertBox.style.borderRadius = "6px";
        alertBox.style.fontWeight = "500";
        alertBox.style.transition = "opacity 3.5s ease";
        document.querySelector(".modal-content").prepend(alertBox);
    }

    // Nustatome stilių pagal tipą
    if (type === "error") {
        alertBox.style.background = "#fee2e2";
        alertBox.style.color = "#991b1b";
        alertBox.style.border = "1px solid #ef4444";
    } else {
        alertBox.style.background = "#dcfce7";
        alertBox.style.color = "#166534";
        alertBox.style.border = "1px solid #22c55e";
    }

    alertBox.textContent = message;
    alertBox.style.opacity = "1";

    // Automatinis pranykimas po 3 sekundžių
    setTimeout(() => {
        alertBox.style.opacity = "0";
        setTimeout(() => alertBox.remove(), 500);
    }, 3000);
}

// --- GYVENTOJO PROFILIO FORMOS LOGIKA ---
document.addEventListener("DOMContentLoaded", () => {
    const profileForm = document.getElementById("profileForm");
    if (profileForm) {
        console.log("🟢 Gyventojo profilio forma aptikta, pridedamas listeneris...");
        profileForm.addEventListener("submit", handleResidentProfileSubmit);
    }
});

function handleResidentProfileSubmit(e) {
    e.preventDefault();

    const form = e.target;
    const userId = form.dataset.userId;
    const formData = new FormData(form);
    const jsonData = Object.fromEntries(formData.entries());
    const messageBox = document.getElementById("profileMessage");

    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    // Paprasta validacija prieš siuntimą
    if (!jsonData.email || !jsonData.email.includes("@")) {
        return showProfileMessage("Neteisingas el. paštas.", "error");
    }
    if (jsonData.password && jsonData.password.length < 6) {
        return showProfileMessage("Slaptažodis turi būti bent 6 simbolių.", "error");
    }

    fetch(`/users/${userId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            [header]: token
        },
        body: JSON.stringify(jsonData)
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                showProfileMessage(data.success, "success");

                // pasirinktinai: jei keičiamas el. paštas, atsijungiam automatiškai
                if (jsonData.email && jsonData.email !== document.body.dataset.userEmail) {
                    setTimeout(() => {
                        window.location.href = "/logout";
                    }, 2000);
                }
            } else {
                showProfileMessage(data.error || "Klaida atnaujinant profilį.", "error");
            }
        })
        .catch(err => showProfileMessage("Tinklo klaida: " + err.message, "error"));
}

function showProfileMessage(message, type = "success") {
    let box = document.getElementById("profileMessage");
    if (!box) return;

    box.style.padding = "10px";
    box.style.marginTop = "10px";
    box.style.borderRadius = "6px";
    box.style.fontWeight = "500";
    box.style.transition = "opacity 3s ease";

    if (type === "error") {
        box.style.background = "#fee2e2";
        box.style.color = "#991b1b";
        box.style.border = "1px solid #ef4444";
    } else {
        box.style.background = "#dcfce7";
        box.style.color = "#166534";
        box.style.border = "1px solid #22c55e";
    }

    box.textContent = message;
    box.style.opacity = "1";

    setTimeout(() => {
        box.style.opacity = "0";
        setTimeout(() => {
            box.textContent = "";
            box.style.padding = "0";
            box.style.border = "none";
        }, 500);
    }, 3000);
}




