# ✦ Masjid Management System - Offline Desktop App Guide (Java Swing)

This is the documentation for the **Offline Desktop Application** built with Java Swing. It is designed to run 100% offline on any Windows, macOS, or Linux laptop/desktop. It saves all records locally into CSV files under the `data/` folder (requires **no** internet, databases, or cloud accounts!).

---

## 🚀 How to Run locally

### Method 1: Run via Command Line
Open PowerShell or Terminal in the project root folder and execute:
```bash
# 1. Compile all Java code
javac -d out src/masjid/model/*.java src/masjid/interfaces/*.java src/masjid/manager/*.java src/masjid/gui/*.java src/masjid/MasjidApp.java

# 2. Run the compiled application
java -cp out masjid.MasjidApp
```

---

## 📦 How to Package into a Double-Clickable `.jar`
To bundle the Java classes into a single executable `.jar` file that you can double-click to launch:

If `jar` is globally recognized:
```bash
jar cfe MasjidApp.jar masjid.MasjidApp -C out .
```

If `jar` is not recognized (Windows JDK 17 Path):
```powershell
& "C:\Program Files\Java\jdk-17\bin\jar.exe" cfe MasjidApp.jar masjid.MasjidApp -C out .
```
You can now run `MasjidApp.jar` on any laptop with Java installed!

---

## 💻 How to Package into a Windows `.exe`
To wrap your `.jar` inside a native Windows `.exe` (which runs without showing a command prompt window):
1. Download **Launch4j** (free).
2. Set the **Output file** to `MasjidApp.exe`.
3. Set the **Jar path** to your generated `MasjidApp.jar`.
4. Under the **JRE** tab, set **Min JRE version** to `1.8.0`.
5. Click **Build** to produce your standalone `MasjidApp.exe`!

---

## 📂 Flat-File Storage
All data is stored in standard, easily editable flat `.csv` files under the local `./data` folder:
- `data/donors.csv`
- `data/donations.csv`
- `data/announcements.csv`
- `data/staff.csv`
- `data/namaz.csv`
