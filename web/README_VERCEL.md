# ✦ Masjid Management System - Vercel Deployment & Live Web Preview

This directory contains a **fully interactive, responsive, and beautifully designed Web Preview** of your Masjid Management System. It exactly replicates the features of your Java Swing desktop application:
- **Interactive Dashboard**: System overview statistics cards for Donors, Donations, Total collections, Announcements, and Staff.
- **Congregation Prayer Timings Editor**: Fully editable congregation times for Fajr, Dhuhr, Asr, Maghrib, Isha, and Jummah that persist inside your browser's LocalStorage.
- **Donation Collections Calendar**: Active collection days are highlighted in soft green; clicking any day aggregates collections and displays the exact donor name, purpose, and amount in a scrollable details box.
- **Interactive CRUD Tabs**: Full Create, Read, Update, Search, and Delete controls for **Donors**, **Donations**, **Announcements**, and **Staff** with smooth lists, forms, and tables.
- **Persistent Storage**: Uses LocalStorage so all changes you make live inside the browser remain saved when you close or refresh the tab!

---

## 🚀 How to Deploy to Vercel (1-Minute Drag & Drop)

You can publish this system live on the web so users can access it on their **mobiles, tablets, or laptops** with these simple steps:

### Method 1: Vercel Web Dashboard (Easiest)
1. Go to [https://vercel.com](https://vercel.com) and log in (or sign up for a free Hobby account using GitHub or Email).
2. Go to your **Projects** page.
3. Scroll down to the **"Drag & Drop"** section (or go to `vercel.com/new`).
4. Simply drag the **`web`** folder from your computer and drop it into the deployment box.
5. Vercel will deploy it in seconds and give you a live **`.vercel.app`** URL (e.g., `masjid-system.vercel.app`) that you can share with anyone!

### Method 2: Vercel CLI (For Terminal Users)
If you have Node.js and the Vercel CLI installed:
1. Open PowerShell / Command Prompt and navigate to this folder:
   ```bash
   cd c:\Users\khani\StudioProjects\masjid\web
   ```
2. Run the deployment command:
   ```bash
   vercel
   ```
3. Follow the terminal prompts (press Enter to accept default settings). Once finished, it will give you your live URL!

---

## 💻 How to Run the Web Preview Locally
If you want to view it locally on your computer before deploying:
1. Double-click the **`index.html`** file inside this `web` folder to open it instantly in any browser (Chrome, Edge, Firefox, Safari).
2. That's it! Everything works out-of-the-box with full interactive LocalStorage support.

---

## ☕ How to Run the Java Swing Desktop App
To run the native Java desktop application:
1. Double-click the **`run.bat`** file in the root project folder (`c:\Users\khani\StudioProjects\masjid`).
2. The batch file will automatically compile your source files inside `src` and launch the native Java Swing GUI.
