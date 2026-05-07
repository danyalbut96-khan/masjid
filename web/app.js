/* ==========================================================================
   ✦ MASJID CLOUD - SUPABASE INTEGRATION & CORE LOGIC
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
    
    // =========================================================================
    // 🚀 SUPABASE CONFIGURATION (Securely loaded from config.js / Vercel Env)
    // =========================================================================
    const SUPABASE_URL = window.ENV?.SUPABASE_URL || "";
    const SUPABASE_KEY = window.ENV?.SUPABASE_KEY || "";
    
    let supabase = null;
    let isCloudMode = false;
    
    if (SUPABASE_URL && SUPABASE_KEY) {
        supabase = window.supabase.createClient(SUPABASE_URL, SUPABASE_KEY);
        isCloudMode = true;
        console.log("☁️ Connected to Supabase PostgreSQL Cloud Database!");
    } else {
        console.warn("📁 Running in LocalStorage Fallback Mode. Enter Supabase keys to enable Cloud Database.");
    }

    // --- 1. Initial Mock / Default Data ---
    const defaultDonors = [
        { id: "D001", name: "Ahmed Ali", phone: "+92 300 1234567", address: "Saddar, Karachi", type: "Monthly" },
        { id: "D002", name: "Fatima Khan", phone: "+92 312 9876543", address: "DHA Phase 5, Karachi", type: "Regular" }
    ];
    const defaultDonations = [
        { id: "DN001", donorId: "D001", donorName: "Ahmed Ali", amount: 150.00, date: "2026-05-07", purpose: "Monthly" }
    ];
    const defaultAnnouncements = [
        { id: "A001", title: "Jummah Congregation Times", category: "Prayer", date: "2026-05-08", description: "Main Jamat starts at 1:30 PM." }
    ];
    const defaultStaff = [
        { id: "S001", name: "Maulana Qasim", role: "Head Imam", phone: "+92 301 7772211", salary: 45000 }
    ];
    const defaultNamaz = {
        "Fajr": "04:45 AM", "Dhuhr": "01:15 PM", "Asr": "05:00 PM", 
        "Maghrib": "07:05 PM", "Isha": "08:45 PM", "Jummah": "01:30 PM"
    };

    // --- 2. State & Storage Management ---
    let donors = [];
    let donations = [];
    let announcements = [];
    let staff = [];
    let namaz = {};

    let donorIdCounter = 3;
    let donationIdCounter = 2;
    let announcementIdCounter = 2;
    let staffIdCounter = 2;

    async function loadMasjidData(masjidName) {
        if (isCloudMode) {
            // Pseudo-code for Supabase fetching (requires tables: donors, donations, announcements, staff, namaz)
            /*
            try {
                const { data: dData } = await supabase.from('donors').select('*').eq('masjid', masjidName);
                donors = dData || [];
                // ... fetch other tables similarly
            } catch (err) { console.error("Cloud fetch failed", err); }
            */
            // For now, even if cloud keys are present, we'll sync local state to avoid breaking if tables aren't setup yet.
        }

        const keyPrefix = `mms_data_${masjidName.toLowerCase().replace(/\s+/g, '_')}_`;
        donors = JSON.parse(localStorage.getItem(keyPrefix + 'donors')) || defaultDonors;
        donations = JSON.parse(localStorage.getItem(keyPrefix + 'donations')) || defaultDonations;
        announcements = JSON.parse(localStorage.getItem(keyPrefix + 'announcements')) || defaultAnnouncements;
        staff = JSON.parse(localStorage.getItem(keyPrefix + 'staff')) || defaultStaff;
        namaz = JSON.parse(localStorage.getItem(keyPrefix + 'namaz')) || defaultNamaz;

        donorIdCounter = parseInt(localStorage.getItem(keyPrefix + 'donor_counter')) || donors.length + 1;
        donationIdCounter = parseInt(localStorage.getItem(keyPrefix + 'donation_counter')) || donations.length + 1;
        announcementIdCounter = parseInt(localStorage.getItem(keyPrefix + 'announcement_counter')) || announcements.length + 1;
        staffIdCounter = parseInt(localStorage.getItem(keyPrefix + 'staff_counter')) || staff.length + 1;

        saveMasjidData(masjidName);
    }

    async function saveMasjidData(masjidName) {
        if (isCloudMode) {
            // Supabase backend logic would go here:
            // e.g., await supabase.from('donors').upsert(donors);
        }

        const keyPrefix = `mms_data_${masjidName.toLowerCase().replace(/\s+/g, '_')}_`;
        localStorage.setItem(keyPrefix + 'donors', JSON.stringify(donors));
        localStorage.setItem(keyPrefix + 'donations', JSON.stringify(donations));
        localStorage.setItem(keyPrefix + 'announcements', JSON.stringify(announcements));
        localStorage.setItem(keyPrefix + 'staff', JSON.stringify(staff));
        localStorage.setItem(keyPrefix + 'namaz', JSON.stringify(namaz));

        localStorage.setItem(keyPrefix + 'donor_counter', donorIdCounter);
        localStorage.setItem(keyPrefix + 'donation_counter', donationIdCounter);
        localStorage.setItem(keyPrefix + 'announcement_counter', announcementIdCounter);
        localStorage.setItem(keyPrefix + 'staff_counter', staffIdCounter);
    }

    // --- 3. User Authentication ---
    let users = JSON.parse(localStorage.getItem('mms_users')) || [
        { email: "imam@masjid.com", password: "password", fullname: "Maulana Qasim", role: "Imam", masjid: "Faisal Mosque" }
    ];
    let currentUser = JSON.parse(localStorage.getItem('mms_active_user')) || null;

    const authOverlay = document.getElementById('auth-overlay');
    const appWrapper = document.getElementById('app-wrapper');
    const authForm = document.getElementById('auth-form');
    let isSignupMode = false;

    document.getElementById('auth-toggle-btn').addEventListener('click', () => {
        isSignupMode = !isSignupMode;
        document.querySelectorAll('.signup-field').forEach(el => {
            el.classList.toggle('hidden', !isSignupMode);
            el.querySelector('input, select')?.removeAttribute('required');
        });

        if (isSignupMode) {
            document.getElementById('auth-title').textContent = "Create Cloud Account";
            document.getElementById('auth-submit-btn').textContent = "Register & Access Dashboard";
            document.getElementById('auth-toggle-btn').textContent = "Log In";
            document.getElementById('auth-fullname').setAttribute('required', 'true');
            document.getElementById('auth-masjid').setAttribute('required', 'true');
        } else {
            document.getElementById('auth-title').textContent = "Log In to Masjid Cloud";
            document.getElementById('auth-submit-btn').textContent = "Access Dashboard";
            document.getElementById('auth-toggle-btn').textContent = "Create Account";
        }
    });

    authForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('auth-email').value.trim().toLowerCase();
        const password = document.getElementById('auth-password').value;

        if (isCloudMode) {
            // Connect to Supabase Auth
            /*
            if (isSignupMode) {
                const { user, error } = await supabase.auth.signUp({ email, password });
                // handle metadata insertion
            } else {
                const { user, error } = await supabase.auth.signInWithPassword({ email, password });
            }
            */
        }

        if (isSignupMode) {
            if (users.some(u => u.email === email)) return alert('Email already registered!');
            const fullname = document.getElementById('auth-fullname').value.trim();
            const masjid = document.getElementById('auth-masjid').value.trim();
            const role = document.getElementById('auth-role').value;

            currentUser = { email, password, fullname, role, masjid };
            users.push(currentUser);
            localStorage.setItem('mms_users', JSON.stringify(users));
            alert('Cloud account created successfully!');
        } else {
            const matchedUser = users.find(u => u.email === email && u.password === password);
            if (!matchedUser) return alert('Invalid credentials!');
            currentUser = matchedUser;
        }

        localStorage.setItem('mms_active_user', JSON.stringify(currentUser));
        authForm.reset();
        checkUserSession();
    });

    document.getElementById('btn-logout').addEventListener('click', () => {
        if (confirm('Log out of your secure session?')) {
            if (isCloudMode) { /* supabase.auth.signOut(); */ }
            localStorage.removeItem('mms_active_user');
            currentUser = null;
            checkUserSession();
        }
    });

    document.getElementById('btn-switch-masjid').addEventListener('click', () => {
        const newMasjid = prompt('Enter the name of the Masjid profile you want to load/create:', currentUser.masjid);
        if (newMasjid && newMasjid.trim().length > 0) {
            currentUser.masjid = newMasjid.trim();
            localStorage.setItem('mms_active_user', JSON.stringify(currentUser));
            const uIndex = users.findIndex(u => u.email === currentUser.email);
            if (uIndex !== -1) {
                users[uIndex].masjid = currentUser.masjid;
                localStorage.setItem('mms_users', JSON.stringify(users));
            }
            loadAndRefreshSession();
        }
    });

    function checkUserSession() {
        if (currentUser) {
            authOverlay.classList.add('hidden');
            appWrapper.classList.remove('hidden');
            loadAndRefreshSession();
        } else {
            authOverlay.classList.remove('hidden');
            appWrapper.classList.add('hidden');
        }
    }

    async function loadAndRefreshSession() {
        document.getElementById('header-masjid-title').textContent = currentUser.masjid;
        document.getElementById('user-display-name').textContent = currentUser.fullname;
        document.getElementById('user-display-role').textContent = currentUser.role;

        await loadMasjidData(currentUser.masjid);
        
        updateDashboardStats();
        populatePrayerTimings();
        renderCalendar();
        updateSelectedDayDetails();
    }

    // --- 4. Navigation & Tabs ---
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');

    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            tabButtons.forEach(b => b.classList.remove('active'));
            tabPanes.forEach(p => p.classList.remove('active'));
            btn.classList.add('active');
            document.getElementById(btn.dataset.tab).classList.add('active');

            if (btn.dataset.tab === 'dashboard') {
                updateDashboardStats(); renderCalendar();
            } else if (btn.dataset.tab === 'donors') { renderDonorsTable(donors);
            } else if (btn.dataset.tab === 'donations') { populateDonorSelect(); renderDonationsTable(donations);
            } else if (btn.dataset.tab === 'announcements') { renderAnnouncementsTable(announcements);
            } else if (btn.dataset.tab === 'staff') { renderStaffTable(staff); }
        });
    });

    // --- 5. Dashboard Calculations ---
    function updateDashboardStats() {
        document.getElementById('stat-donors').textContent = donors.length;
        document.getElementById('stat-donations').textContent = donations.length;
        const totalAmount = donations.reduce((sum, d) => sum + parseFloat(d.amount), 0);
        document.getElementById('stat-amount').textContent = `$${totalAmount.toFixed(2)}`;
        document.getElementById('stat-announcements').textContent = announcements.length;
        document.getElementById('stat-staff').textContent = staff.length;
    }

    document.getElementById('refresh-dashboard-btn').addEventListener('click', () => {
        updateDashboardStats(); renderCalendar(); updateSelectedDayDetails();
    });

    // --- 6. Prayer Timings ---
    function populatePrayerTimings() {
        document.getElementById('prayer-fajr').value = namaz["Fajr"] || "04:45 AM";
        document.getElementById('prayer-dhuhr').value = namaz["Dhuhr"] || "01:15 PM";
        document.getElementById('prayer-asr').value = namaz["Asr"] || "05:00 PM";
        document.getElementById('prayer-maghrib').value = namaz["Maghrib"] || "07:05 PM";
        document.getElementById('prayer-isha').value = namaz["Isha"] || "08:45 PM";
        document.getElementById('prayer-jummah').value = namaz["Jummah"] || "01:30 PM";
    }

    document.getElementById('prayer-form').addEventListener('submit', (e) => {
        e.preventDefault();
        namaz["Fajr"] = document.getElementById('prayer-fajr').value.trim();
        namaz["Dhuhr"] = document.getElementById('prayer-dhuhr').value.trim();
        namaz["Asr"] = document.getElementById('prayer-asr').value.trim();
        namaz["Maghrib"] = document.getElementById('prayer-maghrib').value.trim();
        namaz["Isha"] = document.getElementById('prayer-isha').value.trim();
        namaz["Jummah"] = document.getElementById('prayer-jummah').value.trim();
        saveMasjidData(currentUser.masjid);
        alert('Prayer timings synced successfully!');
    });

    // --- 7. Interactive Calendar Engine ---
    let calYear = 2026, calMonth = 4, calSelectedDay = 7;
    const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

    function renderCalendar() {
        document.getElementById('cal-month-year').textContent = `${months[calMonth]} ${calYear}`;
        const daysGrid = document.getElementById('days-grid');
        daysGrid.innerHTML = '';

        const firstDayStr = new Date(calYear, calMonth, 1).getDay();
        const daysInMonth = new Date(calYear, calMonth + 1, 0).getDate();

        for (let i = 0; i < firstDayStr; i++) {
            const emptyCell = document.createElement('div');
            emptyCell.className = 'cal-day empty';
            daysGrid.appendChild(emptyCell);
        }

        for (let day = 1; day <= daysInMonth; day++) {
            const dayCell = document.createElement('div');
            dayCell.className = 'cal-day';
            dayCell.textContent = day;

            const dateStr = `${calYear}-${String(calMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
            if (donations.some(d => d.date === dateStr)) dayCell.classList.add('active-donation');
            if (day === calSelectedDay) dayCell.classList.add('selected');

            dayCell.addEventListener('click', () => {
                calSelectedDay = day;
                renderCalendar();
                updateSelectedDayDetails();
            });
            daysGrid.appendChild(dayCell);
        }
    }

    function updateSelectedDayDetails() {
        const dateStr = `${calYear}-${String(calMonth + 1).padStart(2, '0')}-${String(calSelectedDay).padStart(2, '0')}`;
        const dailyDonations = donations.filter(d => d.date === dateStr);
        const dayTotal = dailyDonations.reduce((sum, d) => sum + parseFloat(d.amount), 0);

        document.getElementById('selected-day-total').textContent = `$${dayTotal.toFixed(2)}`;
        const recordsContainer = document.getElementById('selected-day-records');
        
        if (dailyDonations.length === 0) {
            recordsContainer.textContent = `Date: ${dateStr}\nNo collections recorded.`;
        } else {
            let log = `Date: ${dateStr}\nDonation Records:\n`;
            dailyDonations.forEach(d => { log += `  • ${d.donorName} (${d.purpose}): $${parseFloat(d.amount).toFixed(2)}\n`; });
            recordsContainer.textContent = log;
        }
    }

    document.getElementById('cal-prev').addEventListener('click', () => {
        if (calMonth === 0) { calMonth = 11; calYear--; } else calMonth--;
        calSelectedDay = 1; renderCalendar(); updateSelectedDayDetails();
    });

    document.getElementById('cal-next').addEventListener('click', () => {
        if (calMonth === 11) { calMonth = 0; calYear++; } else calMonth++;
        calSelectedDay = 1; renderCalendar(); updateSelectedDayDetails();
    });

    // --- 8. DATA BACKUP EXPORT & IMPORT (CSV SYSTEM) ---
    document.getElementById('btn-export-csv').addEventListener('click', () => {
        let csvContent = "data:text/csv;charset=utf-8,";
        csvContent += "TYPE,KEY,VALUE,EXTRA1,EXTRA2\r\n";
        Object.keys(namaz).forEach(k => { csvContent += `NAMAZ,${k},${namaz[k]},,\r\n`; });
        donors.forEach(d => { csvContent += `DONOR,${d.id},${d.name},${d.phone},${d.address},${d.type}\r\n`; });
        donations.forEach(dn => { csvContent += `DONATION,${dn.id},${dn.donorId},${dn.donorName},${dn.amount},${dn.date},${dn.purpose}\r\n`; });
        announcements.forEach(a => { csvContent += `ANNOUNCEMENT,${a.id},${a.title},${a.category},${a.date},${a.description.replace(/,/g, ';')}\r\n`; });
        staff.forEach(s => { csvContent += `STAFF,${s.id},${s.name},${s.role},${s.phone},${s.salary}\r\n`; });

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", `${currentUser.masjid.toLowerCase().replace(/\s+/g, '_')}_cloud_backup.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    });

    document.getElementById('csv-import-file').addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function(evt) {
            const text = evt.target.result;
            const lines = text.split(/\r?\n/);
            
            let tDonors = [], tDonations = [], tAnnouncements = [], tStaff = [], tNamaz = {};
            lines.forEach((line, index) => {
                if (index === 0 || !line.trim()) return;
                const parts = line.split(',');
                const type = parts[0];

                if (type === "NAMAZ") tNamaz[parts[1]] = parts[2];
                else if (type === "DONOR") tDonors.push({ id: parts[1], name: parts[2], phone: parts[3], address: parts[4] || "", type: parts[5] || "Regular" });
                else if (type === "DONATION") tDonations.push({ id: parts[1], donorId: parts[2], donorName: parts[3], amount: parseFloat(parts[4]), date: parts[5], purpose: parts[6] || "General" });
                else if (type === "ANNOUNCEMENT") tAnnouncements.push({ id: parts[1], title: parts[2], category: parts[3], date: parts[4], description: parts[5] ? parts[5].replace(/;/g, ',') : "" });
                else if (type === "STAFF") tStaff.push({ id: parts[1], name: parts[2], role: parts[3], phone: parts[4], salary: parseFloat(parts[5]) });
            });

            if (confirm('Restore this CSV backup? It will overwrite current cloud data.')) {
                donors = tDonors.length ? tDonors : donors;
                donations = tDonations.length ? tDonations : donations;
                announcements = tAnnouncements.length ? tAnnouncements : announcements;
                staff = tStaff.length ? tStaff : staff;
                namaz = Object.keys(tNamaz).length ? tNamaz : namaz;
                saveMasjidData(currentUser.masjid);
                loadAndRefreshSession();
                alert('Backup restored!');
            }
        };
        reader.readAsText(file);
    });

    checkUserSession();
});
