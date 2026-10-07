let currentState = null;
let selectedCharger = null;

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("registerForm").addEventListener("submit", registerCustomer);
    document.getElementById("walletForm").addEventListener("submit", addMoney);
    loadState();
});

async function loadState() {
    try {
        const response = await fetch("/api/state");
        currentState = await response.json();

        updateDashboard();
        updateCustomer();
        updateChargers();
        updateReservations();
    } catch(error) {
        showMessage("Unable to connect to Java server.");
    }
}

function updateDashboard() {
    if(!currentState) return;

    const customer = currentState.customer;

    document.getElementById("wallet").textContent =
        "₹" + (customer ? customer.wallet.toFixed(2) : "0.00");

    document.getElementById("availableCount").textContent =
        currentState.availableCount;

    document.getElementById("bookedCount").textContent =
        currentState.bookedCount;

    document.getElementById("revenue").textContent =
        "₹" + currentState.station.revenue.toFixed(2);
}

function updateCustomer() {
    const box = document.getElementById("customerDetails");

    if(!currentState.customer) {
        box.innerHTML = "";
        return;
    }

    const c = currentState.customer;

    box.innerHTML = `
        <p><strong>Name:</strong> ${escapeHTML(c.name)}</p>
        <p><strong>Email:</strong> ${escapeHTML(c.email)}</p>
        <p><strong>Vehicle:</strong> ${escapeHTML(c.vehicle)}</p>
        <p><strong>Vehicle Type:</strong> ${escapeHTML(c.vehicleType)}</p>
    `;
}

function updateChargers() {
    const container = document.getElementById("chargerList");
    container.innerHTML = "";

    if(currentState.chargers.length === 0) {
        container.innerHTML = `<div class="empty">No chargers found.</div>`;
        return;
    }

    currentState.chargers.forEach(charger => {

        const compatible =
            currentState.customer &&
            currentState.customer.vehicleType.toLowerCase() ===
            charger.vehicleType.toLowerCase();

        const card = document.createElement("div");

        card.className =
            "charger" +
            (compatible ? " compatible" : "") +
            (!charger.available ? " unavailable" : "");

        const statusClass = charger.available ? "available" : "booked";
        const statusText = charger.available ? "Available" : "Booked";

        let buttonHTML = "";

        if(charger.available && compatible) {
            buttonHTML =
                `<button onclick="openBooking(${charger.id})">Book Charger</button>`;
        } else if(!charger.available) {
            buttonHTML =
                `<button disabled>Currently Booked</button>`;
        } else {
            buttonHTML =
                `<button disabled>Not Compatible</button>`;
        }

        card.innerHTML = `
            <div class="charger-top">
                <div class="charger-id">#${charger.id}</div>
                <span class="badge ${statusClass}">${statusText}</span>
            </div>

            <div class="charger-info">
                <div class="info-item">
                    <span>Type</span>
                    <strong>${escapeHTML(charger.type)}</strong>
                </div>

                <div class="info-item">
                    <span>Vehicle</span>
                    <strong>${escapeHTML(charger.vehicleType)}</strong>
                </div>

                <div class="info-item">
                    <span>Power</span>
                    <strong>${charger.power} kW</strong>
                </div>

                <div class="info-item">
                    <span>Rate</span>
                    <strong>₹${charger.price}/hr</strong>
                </div>
            </div>

            ${buttonHTML}
        `;

        container.appendChild(card);
    });
}

function updateReservations() {
    const container = document.getElementById("reservationList");
    container.innerHTML = "";

    if(!currentState.customer) {
        container.innerHTML =
            `<div class="empty">Register a customer to view reservations.</div>`;
        return;
    }

    if(currentState.reservations.length === 0) {
        container.innerHTML =
            `<div class="empty">No reservations found.</div>`;
        return;
    }

    currentState.reservations.forEach(reservation => {

        const div = document.createElement("div");
        div.className = "reservation";

        let action = "";

        if(reservation.status === "Booked") {
            action = `
                <button
                    class="cancel-button"
                    onclick="cancelReservation(${reservation.id})">
                    Cancel
                </button>
            `;
        }

        div.innerHTML = `
            <div class="reservation-info">
                <h3>Reservation #${reservation.id}</h3>

                <p>
                    Charger ${reservation.chargerID}
                    · ${escapeHTML(reservation.chargerType)}
                </p>

                <p>
                    ${reservation.hours} hour(s)
                    · ₹${reservation.amount.toFixed(2)}
                </p>

                <p>
                    Status:
                    <strong>${escapeHTML(reservation.status)}</strong>
                </p>
            </div>

            ${action}
        `;

        container.appendChild(div);
    });
}

async function registerCustomer(event) {
    event.preventDefault();

    const data = new URLSearchParams();

    data.append("name", document.getElementById("name").value);
    data.append("email", document.getElementById("email").value);
    data.append("vehicle", document.getElementById("vehicle").value);
    data.append("vehicleType", document.getElementById("vehicleType").value);

    try {
        const response = await fetch("/api/register", {
            method: "POST",
            body: data
        });

        const result = await response.json();

        showMessage(result.message);

        if(result.success) {
            document.getElementById("registerForm").reset();
            await loadState();
        }

    } catch(error) {
        showMessage("Registration failed.");
    }
}

async function addMoney(event) {
    event.preventDefault();

    const data = new URLSearchParams();

    data.append(
        "amount",
        document.getElementById("amount").value
    );

    try {
        const response = await fetch("/api/wallet", {
            method: "POST",
            body: data
        });

        const result = await response.json();

        showMessage(result.message);

        if(result.success) {
            document.getElementById("walletForm").reset();
            await loadState();
        }

    } catch(error) {
        showMessage("Unable to add money.");
    }
}

function openBooking(chargerID) {

    if(!currentState.customer) {
        showMessage("Register a customer first.");
        return;
    }

    selectedCharger =
        currentState.chargers.find(c => c.id === chargerID);

    if(!selectedCharger) {
        showMessage("Charger not found.");
        return;
    }

    document.getElementById("modalTitle").textContent =
        "Book Charger #" + selectedCharger.id;

    document.getElementById("modalContent").innerHTML = `
        <p>
            <strong>${escapeHTML(selectedCharger.type)}</strong>
        </p>

        <p>
            ${selectedCharger.power} kW
            · ₹${selectedCharger.price}/hour
        </p>

        <label>Charging Duration</label>

        <input
            type="number"
            id="bookingHours"
            min="1"
            step="1"
            value="1">

        <button
            style="width:100%;margin-top:20px"
            onclick="confirmBooking()">
            Confirm Booking
        </button>
    `;

    document.getElementById("modal").classList.remove("hidden");
}

function closeModal() {
    document.getElementById("modal").classList.add("hidden");
    selectedCharger = null;
}

async function confirmBooking() {

    if(!selectedCharger) {
        return;
    }

    const hours =
        parseInt(document.getElementById("bookingHours").value);

    if(!hours || hours <= 0) {
        showMessage("Enter a valid charging duration.");
        return;
    }

    const total = selectedCharger.price * hours;

    if(currentState.customer.wallet < total) {
        showMessage(
            "Insufficient wallet balance. Required ₹" +
            total.toFixed(2)
        );
        return;
    }

    const data = new URLSearchParams();

    data.append("chargerID", selectedCharger.id);
    data.append("hours", hours);

    try {

        const response = await fetch("/api/book", {
            method: "POST",
            body: data
        });

        const result = await response.json();

        closeModal();

        showMessage(result.message);

        await loadState();

    } catch(error) {
        showMessage("Booking failed.");
    }
}

async function cancelReservation(reservationID) {

    const confirmed =
        confirm(
            "Are you sure you want to cancel reservation #" +
            reservationID +
            "?"
        );

    if(!confirmed) {
        return;
    }

    const data = new URLSearchParams();

    data.append("reservationID", reservationID);

    try {

        const response = await fetch("/api/cancel", {
            method: "POST",
            body: data
        });

        const result = await response.json();

        showMessage(result.message);

        await loadState();

    } catch(error) {
        showMessage("Cancellation failed.");
    }
}

function showMessage(message) {

    const box = document.getElementById("message");

    box.textContent = message;

    box.classList.remove("hidden");

    setTimeout(() => {
        box.classList.add("hidden");
    }, 3000);
}

function escapeHTML(value) {

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}