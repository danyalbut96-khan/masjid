# ✦ MASJID CLOUD & DESKTOP SYSTEM — COMPREHENSIVE REPOSITORY GUIDE

Welcome to the **Masjid Management System (MMS)** repository. This project features a state-of-the-art **Dual-Architecture design** tailored to serve two distinct operational environments with absolute reliability, modern SaaS aesthetics, and high-performance execution.

---

## 🏛️ Ecosystem Overview

This repository houses two fully independent client applications optimized for different use cases:

```
                          ┌────────────────────────┐
                          │    Repository Root     │
                          └───────────┬────────────┘
                                      │
            ┌─────────────────────────┴─────────────────────────┐
            ▼                                                   ▼
┌───────────────────────┐                           ┌───────────────────────┐
│  Offline Desktop App  │                           │    Online Web SaaS    │
│     (Java Swing)      │                           │  (HTML5 / CSS3 / JS)  │
├───────────────────────┤                           ├───────────────────────┤
│ • 100% Offline        │                           │ • Live Real-time Sync │
│ • Local CSV Flat-Files│                           │ • Supabase PostgreSQL │
│ • Double-Clickable JAR│                           │ • Responsive Mobile   │
│ • Windows Native .EXE │                           │ • Hosted on Vercel    │
└───────────────────────┘                           └───────────────────────┘
```

---

## 🛠️ Part 1: Offline Desktop Application (Java Swing)

The offline client is a high-performance, local-first administration utility written in native Java Swing. It requires **no internet access, cloud databases, or API configurations**, storing all data locally inside standard CSV files.

### 🌟 Key Features
*   **Pure White Light Theme**: Elegant light-accented look with solid black typography and clean, rounded text inputs.
*   **Responsive Tabbed Navigation**: Easily switch between **Dashboard, Donors, Donations, Announcements,** and **Staff** modules.
*   **Flat-File Data Persistence**: Reads and writes directly to local CSV files instantly with zero latency.

### 🚀 Compilation & Running

Open your PowerShell or Terminal in the project root folder and execute:

```powershell
# 1. Compile all Java code into the "out" directory
javac -d out src/masjid/model/*.java src/masjid/interfaces/*.java src/masjid/manager/*.java src/masjid/gui/*.java src/masjid/MasjidApp.java

# 2. Launch the application
java -cp out masjid.MasjidApp
```

### 📦 Bundling into a Double-Clickable `.jar`

To bundle all compiled class files into a single, highly-portable executable `.jar` file:

```powershell
& "C:\\Program Files\\Java\\jdk-17\\bin\\jar.exe" cfe MasjidApp.jar masjid.MasjidApp -C out .
```
*(You can now double-click `MasjidApp.jar` to run it on any laptop with Java installed!)*

### 💻 Wrapping into a Windows Native `.exe`

To compile your `.jar` into a standalone Windows `.exe` that launches without a console window:
1.  Download **Launch4j** (free open-source utility).
2.  Set the **Output file** path to `MasjidApp.exe`.
3.  Set the **Jar path** to your generated `MasjidApp.jar`.
4.  Under the **JRE** tab, set **Min JRE version** to `1.8.0`.
5.  Click **Build** to generate your standalone executable!

### 📂 Storage Layout
All records are saved inside easily auditable `.csv` files inside `./data/`:
*   `data/donors.csv`
*   `data/donations.csv`
*   `data/announcements.csv`
*   `data/staff.csv`
*   `data/namaz.csv`

---

## ☁️ Part 2: Online Web SaaS Application (HTML / CSS / JS)

The online client is an elegant, responsive cloud platform designed for live administrative sync and mobile access. It connects directly to a secure Supabase backend with public Row-Level Security (RLS) bypass policies.

### 🌟 Key Features
*   **Premium SaaS Landing Page**: Modern marketing gateway with feature grids, responsive badges, and smooth entrance transitions.
*   **Real-time Supabase PostgreSQL Sync**: Direct live bindings for logins, signups, and CRUD operations.
*   **Interactive Donation Calendar**: Features a modern calendar showing date-specific collection totals dynamically.
*   **CSV Backup & Restore**: Instantly download or restore full system backups directly from the browser!

### ⚙️ Database Configuration (Supabase)
1.  Register a free account on [Supabase](https://supabase.com).
2.  Navigate to **SQL Editor** in your database dashboard.
3.  Copy the entire DDL schema from [web/supabase_schema.sql](web/supabase_schema.sql), paste it, and click **Run**.

### 🚀 Direct Cloud Deployment (Vercel)
1.  Link your GitHub repository to your [Vercel](https://vercel.com) account.
2.  Under **Project Settings**, configure:
    *   **Root Directory**: `web`
    *   **Build Command**: `node build.js` *(This securely injects variables during build time)*
3.  Add the following **Environment Variables**:
    *   `SUPABASE_URL` | *Your Supabase Project URL*
    *   `SUPABASE_KEY` | *Your Supabase Public/Anon API Key*
4.  Click **Deploy**! Vercel will build your static files and securely inject variables into an ignored `config.js` file automatically.

### 💻 Local Web Testing
1.  Navigate into the `web` folder.
2.  Create a copy of `config.example.js` and rename it to `config.js`.
3.  Paste your Supabase credentials inside `config.js`.
4.  *(Since `config.js` is declared inside `.gitignore`, your keys will **never** leak to your public GitHub repository!)*

---

## 📂 Project Structure Tree

```
masjid/
├── data/                    # Offline CSV Database Storage
│   ├── donors.csv
│   └── ...
├── src/                     # Java Desktop Source Code
│   └── masjid/
│       ├── gui/             # Swing Panels & StyleUtil Design Tokens
│       ├── manager/         # Business Logic Managers
│       ├── model/           # Data Models (Donor, Donation, Staff)
│       └── MasjidApp.java   # App Entrypoint
├── web/                     # Web SaaS & Landing Page Client
│   ├── build.js             # Vercel Secure Variable Build Injector
│   ├── index.html           # Marketing Landing Page & Dashboard UI
│   ├── style.css            # Premium Emerald Responsive SaaS Styling
│   ├── app.js               # Real-Time Supabase Event Bindings
│   ├── vercel.json          # Vercel Deployment Configuration
│   └── supabase_schema.sql  # Database Schema & Security Policies
├── README_OFFLINE.md        # Dedicated Desktop Walkthrough
└── README.md                # (This File) Comprehensive Master Guide
```

---

## 💎 Support & Credits
Developed with ❤️ by the team at [CloudExify](https://cloudexify.site). For customized web development, mobile APK builds, or database migrations, feel free to visit our online site!
