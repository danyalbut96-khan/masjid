# ✦ Masjid Cloud - Online SaaS Client Guide (Vercel & Supabase)

This is the documentation for the **Online Web/Mobile Application** client. This version connects to a secure real-time cloud database (Supabase) and is designed to run live on any browser or mobile phone (as an APK).

---

## 🛠️ Setup 1: Cloud Database Setup (Supabase)

1. Create a free account on [Supabase](https://supabase.com).
2. Create a new database project (e.g. `Masjid Cloud`).
3. Navigate to **SQL Editor** in your Supabase dashboard.
4. Copy the SQL script from [supabase_schema.sql](supabase_schema.sql), paste it, and click **Run**. This will create all the required real-time tables with public testing policies!

---

## 🚀 Setup 2: Live Deployment (Vercel)

1. Commit your files and push your repository to **GitHub**.
2. Log in to [Vercel](https://vercel.com) and click **"Add New"** -> **"Project"**.
3. Import your repository.
4. Under **Configure Project**:
   - Set **Root Directory** to `web`
   - Turn ON **Build Command** override and type: `node build.js`
   - Under **Environment Variables**, add:
     - Name: `SUPABASE_URL` | Value: *Your Supabase Project URL*
     - Name: `SUPABASE_KEY` | Value: *Your Supabase Anon/Public Key*
5. Click **Deploy**. Your secure live cloud application is ready!

---

## 💻 Local Testing & Environment Variables

To test your web client locally on your computer:
1. Make a copy of [config.example.js](config.example.js) and name it `config.js`.
2. Open `config.js` and paste your Supabase URL and Key inside.
3. Because `config.js` is included in our `.gitignore` file, it will **never** be committed or pushed to your public GitHub repository!
