const BASE_URL = 'http://localhost:8080/api';
let currentSessionId = null;
let loggedInUserId = null;
let loggedInUsername = "";
let isLoginMode = true;

// 1. AUTH: Modus wechseln (Login <-> Register)
function toggleAuthMode() {
    isLoginMode = !isLoginMode;
    document.getElementById('auth-subtitle').innerText = isLoginMode ? 'Melde dich an, um dein Training zu tracken' : 'Erstelle ein neues Konto für dein Tracking';
    document.getElementById('auth-btn').innerText = isLoginMode ? 'Einloggen' : 'Registrieren';
    document.getElementById('toggleAuthBtn').innerText = isLoginMode ? 'Noch kein Konto? Jetzt registrieren' : 'Bereits registriert? Zum Login';
}

// 2. AUTH: Login oder Registrierung an das Backend abschicken
async function handleAuth(event) {
    event.preventDefault();
    const user = document.getElementById('username').value;
    const pass = document.getElementById('password').value;
    const alertBox = document.getElementById('authAlert');

    const endpoint = isLoginMode ? '/users/login' : '/users/register';

    try {
        const response = await fetch(`${BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: user, password: pass })
        });

        if (response.ok) {
            const data = await response.json();

            if (isLoginMode) {
                // Da dein Login-Endpunkt ein boolean (true/false) zurückgibt:
                if (data === true) {
                    // Da wir für die Abfragen eine User-ID brauchen, nutzen wir hier temporär ID 1 (aus deinem CommandLineRunner)
                    // Sobald dein Login-Endpunkt das echte User-Objekt wirft, kannst du data.id nutzen!
                    loggedInUserId = 1;
                    loggedInUsername = user;
                    enterApp();
                } else {
                    showAuthAlert('Falscher Benutzername oder Passwort!', true);
                }
            } else {
                showAuthAlert('Registrierung erfolgreich! Du kannst dich jetzt einloggen.', false);
                toggleAuthMode();
                document.getElementById('password').value = '';
            }
        } else {
            showAuthAlert(isLoginMode ? 'Login fehlgeschlagen.' : 'Username bereits vergeben!', true);
        }
    } catch (err) {
        showAuthAlert('Keine Verbindung zum Backend.', true);
    }
}

function showAuthAlert(msg, isError) {
    const box = document.getElementById('authAlert');
    box.innerText = msg;
    box.className = `p-3 rounded-lg text-sm font-medium text-center ${isError ? 'bg-rose-950 text-rose-400 border border-rose-800' : 'bg-emerald-950 text-emerald-400 border border-emerald-800'}`;
    box.classList.remove('hidden');
}

// 3. APP NAVIGATION: Nach dem Login das Dashboard freischalten
function enterApp() {
    document.getElementById('auth-container').classList.add('hidden');
    document.getElementById('app-container').classList.remove('hidden');
    document.getElementById('userStatus').innerText = `👤 ${loggedInUsername}`;
    loadUserPlans(); // Lädt sofort die Pläne für die Dropdowns
}

function logout() {
    loggedInUserId = null;
    currentSessionId = null;
    document.getElementById('app-container').classList.add('hidden');
    document.getElementById('auth-container').classList.remove('hidden');
    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
    document.getElementById('authAlert').classList.add('hidden');
}

// 4. UI TABS SWITCHEN
function switchTab(tabName) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.getElementById('tab-' + tabName).classList.add('active');
}

function showAlert(text, isError = false) {
    const box = document.getElementById('alertBox');
    box.innerText = text;
    box.className = `mb-6 p-4 rounded-lg font-medium text-sm transition-all ${isError ? 'bg-rose-950 text-rose-400 border border-rose-800' : 'bg-emerald-950 text-emerald-400 border border-emerald-800'}`;
    box.classList.remove('hidden');
    setTimeout(() => box.classList.add('hidden'), 5000);
}

// 5. DROP DOWNS MIT ERSTELLTEN PLÄNEN BEFÜLLEN
async function loadUserPlans() {
    try {
        const response = await fetch(`${BASE_URL}/workouts/plans?userId=${loggedInUserId}`);
        if (response.ok) {
            const plans = await response.json();
            const exeSelect = document.getElementById('exePlanSelect');
            const startSelect = document.getElementById('startPlanSelect');

            exeSelect.innerHTML = '<option value="">-- Bitte wählen --</option>';
            startSelect.innerHTML = '<option value="">-- Bitte wählen --</option>';

            plans.forEach(plan => {
                exeSelect.innerHTML += `<option value="${plan.id}">${plan.name}</option>`;
                startSelect.innerHTML += `<option value="${plan.id}">${plan.name}</option>`;
            });
        }
    } catch (err) { console.error("Fehler beim Laden der Dropdowns", err); }
}

// 6. WORKOUT-PLAN ERSTELLEN
async function createPlan() {
    const name = document.getElementById('planNameInput').value;
    if(!name) return showAlert('Bitte Plannamen eingeben', true);

    try {
        const response = await fetch(`${BASE_URL}/workouts/create-plan`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId: loggedInUserId, name: name })
        });
        if(response.ok) {
            showAlert(`Plan "${name}" erfolgreich erstellt!`);
            document.getElementById('planNameInput').value = '';
            loadUserPlans(); // Dropdowns sofort live aktualisieren, damit der neue Plan sichtbar ist!
        } else { showAlert('Fehler beim Erstellen des Plans.', true); }
    } catch (err) { showAlert('Server-Fehler.', true); }
}

// 7. ÜBUNG ERSTELLEN
async function createExercise() {
    const planId = document.getElementById('exePlanSelect').value;
    const name = document.getElementById('exeName').value;
    const desc = document.getElementById('exeDesc').value;

    if(!planId || !name) return showAlert('Wähle einen Plan und gib einen Namen ein!', true);

    try {
        const response = await fetch(`${BASE_URL}/workouts/create-exercise`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ planId: planId, exerciseName: name, exerciseDescription: desc })
        });
        if(response.ok) {
            const data = await response.json();
            showAlert(`Übung "${data.name}" (ID: ${data.id}) erfolgreich für den Plan angelegt!`);
            document.getElementById('exeName').value = '';
            document.getElementById('exeDesc').value = '';
        } else { showAlert('Übung existiert bereits oder ID ist falsch.', true); }
    } catch (err) { showAlert('Server-Fehler.', true); }
}

// 8. SESSION STARTEN
async function startSession() {
    const planId = document.getElementById('startPlanSelect').value;
    if(!planId) return showAlert('Bitte wähle einen Trainingsplan aus!', true);

    try {
        const response = await fetch(`${BASE_URL}/workouts/start?workoutPlanId=${planId}`, { method: 'POST' });
        if(response.ok) {
            const data = await response.json();
            currentSessionId = data.id;

            document.getElementById('sessionStateText').innerHTML = `🟢 Aktiv: <span class="text-amber-500 font-semibold">${data.planName}</span> (Session ID: ${data.id})`;
            document.getElementById('btnStart').classList.add('hidden');
            document.getElementById('startPlanSelect').classList.add('hidden');
            document.getElementById('btnEnd').classList.remove('hidden');
            document.getElementById('sessionActionArea').classList.remove('hidden');
            showAlert('Workout gestartet. Let\'s go! 🏋️‍♂️');

            const exerciseSelect = document.getElementById('setExerciseSelect');
            exerciseSelect.innerHTML = '<option value="">Lade Übungen...</option>';

            const planResponse = await fetch(`${BASE_URL}/workouts/plans?userId=${loggedInUserId}`);
            if (planResponse.ok) {
                const plans = await planResponse.json();
                const currentPlan = plans.find(p => p.id == planId);

                exerciseSelect.innerHTML = '<option value="">-- Übung wählen --</option>';
                if (currentPlan && currentPlan.exercises && currentPlan.exercises.length > 0) {
                    currentPlan.exercises.forEach(exe => {
                        exerciseSelect.innerHTML += `<option value="${exe.exerciseId}">${exe.exerciseName}</option>`;
                    });
                } else {
                    exerciseSelect.innerHTML = '<option value="">Keine Übungen im Plan gefunden</option>';
                }
            }

        } else {
            showAlert('Schutzmechanismus active: Du hast bereits ein geöffnetes Workout!', true);
        }
    } catch (err) { showAlert('Fehler beim Kommunizieren mit dem Server.', true); }
}

// 9. SATZ HINZUFÜGEN
async function addSet() {
    if(!currentSessionId) return showAlert('Keine aktive Session!', true);
    const exeId = document.getElementById('setExerciseSelect').value;
    const weight = document.getElementById('setWeight').value;
    const reps = document.getElementById('setReps').value;

    if(!exeId || !weight || !reps) return showAlert('Alle Felder ausfüllen!', true);

    try {
        const response = await fetch(`${BASE_URL}/workouts/add-set?sessionId=${currentSessionId}&exerciseId=${exeId}&weight=${weight}&reps=${reps}`, { method: 'POST' });
        if(response.ok) {
            const data = await response.json();
            document.getElementById('lastSetBox').classList.remove('hidden');
            document.getElementById('lastSetText').innerText = `Satz ${data.setNumber}: ${data.weight}kg x ${data.repetitions} Wiederholungen`;
            document.getElementById('setReps').value = ''; // Reps leeren für den nächsten Satz
        } else {
            showAlert('Eintrag abgelehnt! Entweder falsche ID, Werte <= 0 oder Session bereits geschlossen.', true);
        }
    } catch (err) { showAlert('Verbindungsabbruch zum Server.', true); }
}

// 10. SESSION BEENDEN
async function endSession() {
    if(!currentSessionId) return;

    try {
        const response = await fetch(`${BASE_URL}/workouts/end?sessionId=${currentSessionId}`, { method: 'POST' });
        if(response.ok) {
            const data = await response.json();
            showAlert(`Workout beendet! Dauer: ${data.timePassed || 0} Minuten. Gute Arbeit!`);

            currentSessionId = null;
            document.getElementById('sessionStateText').innerText = 'Kein Workout aktiv.';
            document.getElementById('btnStart').classList.remove('hidden');
            document.getElementById('startPlanSelect').classList.remove('hidden');
            document.getElementById('btnEnd').classList.add('hidden');
            document.getElementById('sessionActionArea').classList.add('hidden');
            document.getElementById('lastSetBox').classList.add('hidden');
            loadUserPlans();
        }
    } catch (err) { showAlert('Fehler beim Beenden.', true); }
}

// 11. HISTORIE LADEN (Nutzt deine verschachtelte DTO-Struktur)
async function loadHistory() {
    const container = document.getElementById('historyContainer');
    container.innerHTML = '<p class="text-zinc-500 animate-pulse text-center py-8">Lade deine Trainingsdaten...</p>';

    try {
        const response = await fetch(`${BASE_URL}/workouts/history?userId=${loggedInUserId}`);
        if(response.ok) {
            const historyData = await response.json();
            if(historyData.length === 0) {
                container.innerHTML = '<p class="text-zinc-500 text-center py-8">Noch keine Workouts aufgezeichnet.</p>';
                return;
            }

            container.innerHTML = '';

            // STUFE 1: Iteration über die Workout-Sessions
            historyData.forEach(session => {
                const dateOptions = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute:'2-digit' };
                const startTimeFormatted = new Date(session.startTime).toLocaleDateString('de-DE', dateOptions);

                let sessionHtml = `
    <div class="bg-zinc-900 border border-zinc-800 rounded-xl p-5 shadow-md space-y-4">
    <div class="flex justify-between items-start border-b border-zinc-800 pb-3">
    <div>
    <h3 class="text-lg font-bold text-white">${session.planName}</h3>
<p class="text-zinc-500 text-xs">${startTimeFormatted}</p>
</div>
<span class="bg-zinc-800 text-amber-500 text-xs font-bold px-2.5 py-1 rounded-md border border-zinc-700">
                                Dauer: ${session.timePassed || 0} Min
                            </span>
</div>
<div class="space-y-4">
    `;

    // STUFE 2: Iteration über die Übungen innerhalb der Session
    if(session.exercises && session.exercises.length > 0) {
    session.exercises.forEach(exercise => {
        sessionHtml += `
                            <div class="bg-zinc-950 p-3 rounded-lg border border-zinc-850">
                                <h4 class="text-sm font-semibold text-zinc-300 mb-2">🏋️‍♂️ ${exercise.name}</h4>
                                <div class="grid grid-cols-3 gap-2 text-xs text-zinc-500 font-bold border-b border-zinc-800 pb-1 mb-1">
                                    <div>Satz</div>
                                    <div>Gewicht</div>
                                    <div>Reps</div>
                                </div>
                        `;

        // STUFE 3: Iteration über die einzelnen Sätze der Übung
        if(exercise.setHistory && exercise.setHistory.length > 0) {
            exercise.setHistory.forEach(set => {
                sessionHtml += `
                                    <div class="grid grid-cols-3 gap-2 text-sm py-1 border-b border-zinc-900/50 text-zinc-400">
                                        <div class="font-bold text-zinc-600">#${set.setNumber}</div>
                                        <div>${set.weight} kg</div>
                                        <div>${set.reps}</div>
                                    </div>
                                `;
            });
        }
        sessionHtml += `</div>`;
    });
} else {
    sessionHtml += `<p class="text-zinc-600 text-xs italic">Keine Sätze aufgezeichnet.</p>`;
}

    sessionHtml += `</div></div>`;
                container.innerHTML += sessionHtml;
            });
        } else { container.innerHTML = '<p class="text-rose-400 text-center py-8">Fehler beim Laden der Historie.</p>'; }
    } catch (err) { container.innerHTML = '<p class="text-rose-400 text-center py-8">Server offline.</p>'; }
}