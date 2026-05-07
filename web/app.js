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
            try {
                const { data: dData, error: dErr } = await supabase.from('donors').select('*').eq('masjid', masjidName);
                if (dErr) throw dErr;
                donors = dData || [];

                const { data: dnData, error: dnErr } = await supabase.from('donations').select('*').eq('masjid', masjidName);
                if (dnErr) throw dnErr;
                donations = dnData ? dnData.map(dn => ({
                    id: dn.id,
                    donorId: dn.donor_id,
                    donorName: dn.donor_name,
                    amount: parseFloat(dn.amount),
                    date: dn.date,
                    purpose: dn.purpose
                })) : [];

                const { data: aData, error: aErr } = await supabase.from('announcements').select('*').eq('masjid', masjidName);
                if (aErr) throw aErr;
                announcements = aData ? aData.map(a => ({
                    id: a.id,
                    title: a.title,
                    category: a.category,
                    date: a.date,
                    description: a.description
                })) : [];

                const { data: sData, error: sErr } = await supabase.from('staff').select('*').eq('masjid', masjidName);
                if (sErr) throw sErr;
                staff = sData || [];

                const { data: nData, error: nErr } = await supabase.from('namaz').select('*').eq('masjid', masjidName).maybeSingle();
                if (nErr) throw nErr;
                if (nData) {
                    namaz = {
                        "Fajr": nData.fajr,
                        "Dhuhr": nData.dhuhr,
                        "Asr": nData.asr,
                        "Maghrib": nData.maghrib,
                        "Isha": nData.isha,
                        "Jummah": nData.jummah
                    };
                } else {
                    namaz = { ...defaultNamaz };
                    await supabase.from('namaz').insert({
                        masjid: masjidName,
                        fajr: defaultNamaz["Fajr"],
                        dhuhr: defaultNamaz["Dhuhr"],
                        asr: defaultNamaz["Asr"],
                        maghrib: defaultNamaz["Maghrib"],
                        isha: defaultNamaz["Isha"],
                        jummah: defaultNamaz["Jummah"]
                    });
                }

                donorIdCounter = donors.length ? Math.max(...donors.map(d => parseInt(d.id.replace(/\D/g, '')) || 0)) + 1 : 1;
                donationIdCounter = donations.length ? Math.max(...donations.map(dn => parseInt(dn.id.replace(/\D/g, '')) || 0)) + 1 : 1;
                announcementIdCounter = announcements.length ? Math.max(...announcements.map(a => parseInt(a.id.replace(/\D/g, '')) || 0)) + 1 : 1;
                staffIdCounter = staff.length ? Math.max(...staff.map(s => parseInt(s.id.replace(/\D/g, '')) || 0)) + 1 : 1;

                return;
            } catch (err) {
                console.error("Cloud database fetch failed. Falling back to offline local storage.", err);
            }
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
            try {
                await supabase.from('namaz').upsert({
                    masjid: masjidName,
                    fajr: namaz["Fajr"],
                    dhuhr: namaz["Dhuhr"],
                    asr: namaz["Asr"],
                    maghrib: namaz["Maghrib"],
                    isha: namaz["Isha"],
                    jummah: namaz["Jummah"]
                });
                return;
            } catch (err) {
                console.error("Cloud save failed. Falling back to offline local storage backup.", err);
            }
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
            try {
                if (isSignupMode) {
                    const fullname = document.getElementById('auth-fullname').value.trim();
                    const masjid = document.getElementById('auth-masjid').value.trim();
                    const role = document.getElementById('auth-role').value;

                    const { data: existingUser, error: chkErr } = await supabase.from('users').select('email').eq('email', email).maybeSingle();
                    if (chkErr) throw chkErr;
                    if (existingUser) return alert('Email already registered in the cloud database!');

                    currentUser = { email, password, fullname, role, masjid };
                    const { error: insErr } = await supabase.from('users').insert(currentUser);
                    if (insErr) throw insErr;

                    alert('Cloud account created successfully!');
                } else {
                    const { data: matchedUser, error: selErr } = await supabase.from('users').select('*').eq('email', email).eq('password', password).maybeSingle();
                    if (selErr) throw selErr;
                    if (!matchedUser) return alert('Invalid cloud credentials!');
                    currentUser = matchedUser;
                }

                localStorage.setItem('mms_active_user', JSON.stringify(currentUser));
                authForm.reset();
                checkUserSession();
                return;
            } catch (err) {
                console.error("Cloud auth failed:", err);
                return alert("Cloud connection/auth error: " + err.message);
            }
        }

        if (isSignupMode) {
            if (users.some(u => u.email === email)) return alert('Email already registered!');
            const fullname = document.getElementById('auth-fullname').value.trim();
            const masjid = document.getElementById('auth-masjid').value.trim();
            const role = document.getElementById('auth-role').value;

            currentUser = { email, password, fullname, role, masjid };
            users.push(currentUser);
            localStorage.setItem('mms_users', JSON.stringify(users));
            alert('Local account created successfully!');
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
        const landingPage = document.getElementById('landing-page');
        if (currentUser) {
            if (landingPage) landingPage.classList.add('hidden');
            authOverlay.classList.add('hidden');
            appWrapper.classList.remove('hidden');
            loadAndRefreshSession();
        } else {
            if (landingPage) landingPage.classList.remove('hidden');
            authOverlay.classList.add('hidden');
            appWrapper.classList.add('hidden');
        }
    }

    const showLoginGateway = () => {
        const landingPage = document.getElementById('landing-page');
        if (landingPage) landingPage.classList.add('hidden');
        authOverlay.classList.remove('hidden');
    };

    document.getElementById('launch-portal-nav-btn')?.addEventListener('click', showLoginGateway);
    document.getElementById('launch-portal-hero-btn')?.addEventListener('click', showLoginGateway);
    document.getElementById('launch-portal-pricing-btn')?.addEventListener('click', showLoginGateway);

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

    // --- 9. DONORS CRUD CONTROLLER ---
    function renderDonorsTable(list) {
        const tbody = document.querySelector('#donors-table tbody');
        if (!tbody) return;
        tbody.innerHTML = '';
        list.forEach(d => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${d.id}</td><td>${d.name}</td><td>${d.phone}</td><td>${d.address}</td><td>${d.type}</td>`;
            tr.addEventListener('click', () => selectDonorRow(tr, d));
            tbody.appendChild(tr);
        });
    }

    function selectDonorRow(tr, d) {
        document.querySelectorAll('#donors-table tr').forEach(r => r.classList.remove('selected'));
        tr.classList.add('selected');
        document.getElementById('donor-id').value = d.id;
        document.getElementById('donor-name').value = d.name;
        document.getElementById('donor-phone').value = d.phone;
        document.getElementById('donor-address').value = d.address;
        document.getElementById('donor-type').value = d.type;
        document.getElementById('donor-add-btn').textContent = "Update Donor";
    }

    document.getElementById('donor-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('donor-id').value;
        const name = document.getElementById('donor-name').value.trim();
        const phone = document.getElementById('donor-phone').value.trim();
        const address = document.getElementById('donor-address').value.trim();
        const type = document.getElementById('donor-type').value;

        let finalId = id;
        if (id) {
            const index = donors.findIndex(d => d.id === id);
            if (index !== -1) donors[index] = { id, name, phone, address, type };
        } else {
            finalId = `D${String(donorIdCounter++).padStart(3, '0')}`;
            donors.push({ id: finalId, name, phone, address, type });
        }

        if (isCloudMode) {
            try {
                await supabase.from('donors').upsert({ id: finalId, name, phone, address, type, masjid: currentUser.masjid });
            } catch (err) { console.error(err); }
        }

        saveMasjidData(currentUser.masjid);
        renderDonorsTable(donors);
        clearDonorForm();
        alert('Donor saved successfully!');
    });

    document.getElementById('donor-delete-btn').addEventListener('click', async () => {
        const id = document.getElementById('donor-id').value;
        if (!id) return alert('Select a donor to delete.');
        if (confirm('Delete this donor record?')) {
            donors = donors.filter(d => d.id !== id);
            if (isCloudMode) {
                try { await supabase.from('donors').delete().eq('id', id); } catch (err) { console.error(err); }
            }
            saveMasjidData(currentUser.masjid);
            renderDonorsTable(donors);
            clearDonorForm();
            alert('Donor deleted.');
        }
    });

    document.getElementById('donor-clear-btn').addEventListener('click', clearDonorForm);

    function clearDonorForm() {
        document.getElementById('donor-id').value = '';
        document.getElementById('donor-name').value = '';
        document.getElementById('donor-phone').value = '';
        document.getElementById('donor-address').value = '';
        document.getElementById('donor-type').value = 'Regular';
        document.getElementById('donor-add-btn').textContent = "Add Donor";
        document.querySelectorAll('#donors-table tr').forEach(r => r.classList.remove('selected'));
    }

    document.getElementById('donor-search').addEventListener('input', (e) => {
        const kw = e.target.value.toLowerCase();
        const filtered = donors.filter(d => d.name.toLowerCase().includes(kw) || d.id.toLowerCase().includes(kw));
        renderDonorsTable(filtered);
    });

    // --- 10. DONATIONS CRUD CONTROLLER ---
    function populateDonorSelect() {
        const select = document.getElementById('donation-donor');
        if (!select) return;
        select.innerHTML = '<option value="">-- Select Donor --</option>';
        donors.forEach(d => {
            const opt = document.createElement('option');
            opt.value = d.id;
            opt.textContent = `${d.id} - ${d.name}`;
            select.appendChild(opt);
        });
    }

    function renderDonationsTable(list) {
        const tbody = document.querySelector('#donations-table tbody');
        if (!tbody) return;
        tbody.innerHTML = '';
        list.forEach(d => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${d.id}</td><td>${d.donorId}</td><td>${d.donorName}</td><td>$${parseFloat(d.amount).toFixed(2)}</td><td>${d.date}</td><td>${d.purpose}</td>`;
            tr.addEventListener('click', () => selectDonationRow(tr, d));
            tbody.appendChild(tr);
        });
        const totalAmount = list.reduce((sum, d) => sum + parseFloat(d.amount), 0);
        document.getElementById('donation-history-total').textContent = `Total: $${totalAmount.toFixed(2)}`;
    }

    function selectDonationRow(tr, d) {
        document.querySelectorAll('#donations-table tr').forEach(r => r.classList.remove('selected'));
        tr.classList.add('selected');
        document.getElementById('donation-id').value = d.id;
        document.getElementById('donation-donor').value = d.donorId;
        document.getElementById('donation-amount').value = d.amount;
        document.getElementById('donation-date').value = d.date;
        document.getElementById('donation-purpose').value = d.purpose;
        document.getElementById('donation-add-btn').textContent = "Update Donation";
    }

    document.getElementById('donation-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('donation-id').value;
        const donorId = document.getElementById('donation-donor').value;
        const amount = parseFloat(document.getElementById('donation-amount').value);
        const date = document.getElementById('donation-date').value;
        const purpose = document.getElementById('donation-purpose').value;

        const selectedDonor = donors.find(d => d.id === donorId);
        const donorName = selectedDonor ? selectedDonor.name : "Anonymous";

        let finalId = id;
        if (id) {
            const index = donations.findIndex(d => d.id === id);
            if (index !== -1) donations[index] = { id, donorId, donorName, amount, date, purpose };
        } else {
            finalId = `DN${String(donationIdCounter++).padStart(3, '0')}`;
            donations.push({ id: finalId, donorId, donorName, amount, date, purpose });
        }

        if (isCloudMode) {
            try {
                await supabase.from('donations').upsert({ id: finalId, donor_id: donorId, donor_name: donorName, amount, date, purpose, masjid: currentUser.masjid });
            } catch (err) { console.error(err); }
        }

        saveMasjidData(currentUser.masjid);
        renderDonationsTable(donations);
        clearDonationForm();
        alert('Donation recorded successfully!');
    });

    document.getElementById('donation-delete-btn').addEventListener('click', async () => {
        const id = document.getElementById('donation-id').value;
        if (!id) return alert('Select a record to delete.');
        if (confirm('Delete this collection record permanently?')) {
            donations = donations.filter(d => d.id !== id);
            if (isCloudMode) {
                try { await supabase.from('donations').delete().eq('id', id); } catch (err) { console.error(err); }
            }
            saveMasjidData(currentUser.masjid);
            renderDonationsTable(donations);
            clearDonationForm();
            alert('Record deleted.');
        }
    });

    document.getElementById('donation-clear-btn').addEventListener('click', clearDonationForm);

    function clearDonationForm() {
        document.getElementById('donation-id').value = '';
        document.getElementById('donation-donor').value = '';
        document.getElementById('donation-amount').value = '';
        document.getElementById('donation-date').value = new Date().toISOString().substring(0, 10);
        document.getElementById('donation-purpose').value = 'General';
        document.getElementById('donation-add-btn').textContent = "Record Donation";
        document.querySelectorAll('#donations-table tr').forEach(r => r.classList.remove('selected'));
    }

    document.getElementById('donation-search').addEventListener('input', (e) => {
        const kw = e.target.value.toLowerCase();
        const filtered = donations.filter(d => d.donorName.toLowerCase().includes(kw) || d.purpose.toLowerCase().includes(kw));
        renderDonationsTable(filtered);
    });

    // --- 11. ANNOUNCEMENTS CRUD CONTROLLER ---
    function renderAnnouncementsTable(list) {
        const tbody = document.querySelector('#announcements-table tbody');
        if (!tbody) return;
        tbody.innerHTML = '';
        list.forEach(a => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${a.id}</td><td>${a.title}</td><td>${a.category}</td><td>${a.date}</td><td>${a.description}</td>`;
            tr.addEventListener('click', () => selectAnnouncementRow(tr, a));
            tbody.appendChild(tr);
        });
    }

    function selectAnnouncementRow(tr, a) {
        document.querySelectorAll('#announcements-table tr').forEach(r => r.classList.remove('selected'));
        tr.classList.add('selected');
        document.getElementById('announcement-id').value = a.id;
        document.getElementById('ann-title').value = a.title;
        document.getElementById('ann-category').value = a.category;
        document.getElementById('ann-date').value = a.date;
        document.getElementById('ann-desc').value = a.description;
        document.getElementById('ann-add-btn').textContent = "Update Published";
    }

    document.getElementById('announcement-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('announcement-id').value;
        const title = document.getElementById('ann-title').value.trim();
        const category = document.getElementById('ann-category').value;
        const date = document.getElementById('ann-date').value;
        const description = document.getElementById('ann-desc').value.trim();

        let finalId = id;
        if (id) {
            const index = announcements.findIndex(a => a.id === id);
            if (index !== -1) announcements[index] = { id, title, category, date, description };
        } else {
            finalId = `A${String(announcementIdCounter++).padStart(3, '0')}`;
            announcements.push({ id: finalId, title, category, date, description });
        }

        if (isCloudMode) {
            try {
                await supabase.from('announcements').upsert({ id: finalId, title, category, date, description, masjid: currentUser.masjid });
            } catch (err) { console.error(err); }
        }

        saveMasjidData(currentUser.masjid);
        renderAnnouncementsTable(announcements);
        clearAnnouncementForm();
        alert('Announcement saved!');
    });

    document.getElementById('ann-delete-btn').addEventListener('click', async () => {
        const id = document.getElementById('announcement-id').value;
        if (!id) return alert('Select an announcement to delete.');
        if (confirm('Delete this announcement?')) {
            announcements = announcements.filter(a => a.id !== id);
            if (isCloudMode) {
                try { await supabase.from('announcements').delete().eq('id', id); } catch (err) { console.error(err); }
            }
            saveMasjidData(currentUser.masjid);
            renderAnnouncementsTable(announcements);
            clearAnnouncementForm();
            alert('Announcement deleted.');
        }
    });

    document.getElementById('ann-clear-btn').addEventListener('click', clearAnnouncementForm);

    function clearAnnouncementForm() {
        document.getElementById('announcement-id').value = '';
        document.getElementById('ann-title').value = '';
        document.getElementById('ann-category').value = 'General';
        document.getElementById('ann-date').value = new Date().toISOString().substring(0, 10);
        document.getElementById('ann-desc').value = '';
        document.getElementById('ann-add-btn').textContent = "Publish";
        document.querySelectorAll('#announcements-table tr').forEach(r => r.classList.remove('selected'));
    }

    document.getElementById('ann-search').addEventListener('input', (e) => {
        const kw = e.target.value.toLowerCase();
        const filtered = announcements.filter(a => a.title.toLowerCase().includes(kw) || a.category.toLowerCase().includes(kw));
        renderAnnouncementsTable(filtered);
    });

    // --- 12. STAFF CRUD CONTROLLER ---
    function renderStaffTable(list) {
        const tbody = document.querySelector('#staff-table tbody');
        if (!tbody) return;
        tbody.innerHTML = '';
        list.forEach(s => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${s.id}</td><td>${s.name}</td><td>${s.role}</td><td>${s.phone}</td><td>$${parseFloat(s.salary).toFixed(2)}</td>`;
            tr.addEventListener('click', () => selectStaffRow(tr, s));
            tbody.appendChild(tr);
        });
    }

    function selectStaffRow(tr, s) {
        document.querySelectorAll('#staff-table tr').forEach(r => r.classList.remove('selected'));
        tr.classList.add('selected');
        document.getElementById('staff-id').value = s.id;
        document.getElementById('staff-name').value = s.name;
        document.getElementById('staff-role').value = s.role;
        document.getElementById('staff-phone').value = s.phone;
        document.getElementById('staff-salary').value = s.salary;
        document.getElementById('staff-add-btn').textContent = "Update Staff";
    }

    document.getElementById('staff-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const id = document.getElementById('staff-id').value;
        const name = document.getElementById('staff-name').value.trim();
        const role = document.getElementById('staff-role').value.trim();
        const phone = document.getElementById('staff-phone').value.trim();
        const salary = parseFloat(document.getElementById('staff-salary').value);

        let finalId = id;
        if (id) {
            const index = staff.findIndex(s => s.id === id);
            if (index !== -1) staff[index] = { id, name, role, phone, salary };
        } else {
            finalId = `S${String(staffIdCounter++).padStart(3, '0')}`;
            staff.push({ id: finalId, name, role, phone, salary });
        }

        if (isCloudMode) {
            try {
                await supabase.from('staff').upsert({ id: finalId, name, role, phone, salary, masjid: currentUser.masjid });
            } catch (err) { console.error(err); }
        }

        saveMasjidData(currentUser.masjid);
        renderStaffTable(staff);
        clearStaffForm();
        alert('Staff record saved!');
    });

    document.getElementById('staff-delete-btn').addEventListener('click', async () => {
        const id = document.getElementById('staff-id').value;
        if (!id) return alert('Select a record to delete.');
        if (confirm('Delete this staff record?')) {
            staff = staff.filter(s => s.id !== id);
            if (isCloudMode) {
                try { await supabase.from('staff').delete().eq('id', id); } catch (err) { console.error(err); }
            }
            saveMasjidData(currentUser.masjid);
            renderStaffTable(staff);
            clearStaffForm();
            alert('Staff record deleted.');
        }
    });

    document.getElementById('staff-clear-btn').addEventListener('click', clearStaffForm);

    function clearStaffForm() {
        document.getElementById('staff-id').value = '';
        document.getElementById('staff-name').value = '';
        document.getElementById('staff-role').value = '';
        document.getElementById('staff-phone').value = '';
        document.getElementById('staff-salary').value = '';
        document.getElementById('staff-add-btn').textContent = "Add Staff";
        document.querySelectorAll('#staff-table tr').forEach(r => r.classList.remove('selected'));
    }

    document.getElementById('staff-search').addEventListener('input', (e) => {
        const kw = e.target.value.toLowerCase();
        const filtered = staff.filter(s => s.name.toLowerCase().includes(kw) || s.role.toLowerCase().includes(kw));
        renderStaffTable(filtered);
    });

    checkUserSession();
});
