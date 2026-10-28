
# Legacies Screen Printers – Mobile & Backend Operations System

Youtube Link: https://youtu.be/yL6uNkRqnlo?si=manciL88r3y1n0dE

A full-stack operational management system built for **Legacy Screen Printers**, combining a native **Kotlin Android mobile application** with a robust **C# ASP.NET Core Web API** backend connected to a relational **SQL Server** database.

This project was engineered to digitize production-floor workflows, streamline client order tracking, and replace manual order preparation processes with a reliable, end-to-end client-server architecture.

---

## 🚀 Key Features
* **Mobile Order Placement & Tracking:** Native Android interface built with Jetpack Compose allowing staff and clients to place, modify, and monitor print jobs in real time.
* **Secure Authentication & Session Management:** Integrated login, user roles, and secure session handling via Retrofit API endpoints.
* **RESTful Backend API:** C# ASP.NET Core Web API handling database operations, data validation, and secure network tunneling (e.g., via `ngrok` for mobile-to-server connectivity).
* **Inventory & Print Job Workflow:** Digitized tracking for preparation, screen printing production stages, and quality assurance verification standards.

---

## 🛠️ Technology Stack & Architecture
* **Frontend / Mobile:** Kotlin, Jetpack Compose, Material Design 3, Retrofit (REST Client)
* **Backend:** C#, ASP.NET Core Web API, .NET 8, Entity Framework Core
* **Database:** Microsoft SQL Server (Relational Database Design & Management)
* **Networking & Tooling:** HTTP/REST routing, JSON serialization, Git feature-branch version control

---

## 📂 Project Structure
```text
LegaciesScreenPrinters/
│
├── app/src/main/java/com/example/legaciesscreenprintersapk/
│   ├── data/                 # Data models, mock repositories, and local state
│   ├── ui/                   # Jetpack Compose UI screens (Auth, Home, Order Placement, Tracking, Settings)
│   ├── util/                 # Security helpers, session managers, and API client configs
│   └── MainActivity.kt       # Android app entry point and navigation host
│
└── Backend (ASP.NET Core API)/ # C# Web API controllers, Entity Framework contexts, and SQL models
```

---

## ⚙️ Setup & Local Installation

### 1. Running the Backend API
1. Open the C# ASP.NET Core Web API project solution in **Visual Studio**.
2. Update your SQL Server connection string in `appsettings.json`:
   ```json
   "ConnectionStrings": {
     "DefaultConnection": "Server=(localdb)\\mssqllocaldb;Database=LegaciesScreenPrintersDb;Trusted_Connection=True;MultipleActiveResultSets=true"
   }
   ```
3. Run Entity Framework migrations to initialize the database:
   ```bash
   dotnet ef database update
   ```
4. Run the API project (`F5` or `dotnet run`). Take note of the local localhost URL (e.g., `https://localhost:5001`).

### 2. Running the Android Application
1. Open the Android project folder in **Android Studio**.
2. Open `app/src/main/java/com/example/legaciesscreenprintersapk/ui/remote/RetrofitClient.kt`.
3. Update the base URL to point to your running backend API (if testing on an emulator, use `10.0.2.2:5001`, or use an `ngrok` tunnel URL if testing on a physical Android device):
   ```kotlin
   private const val BASE_URL = "https://your-api-url-here/"
   ```
4. Sync Gradle and run the app on an Android Emulator or physical device.

---

## 🔄 Development & Collaboration Workflow
* Managed using clean Git branching strategies for feature development and bug fixes.
* Modular MVVM-inspired separation of concerns on the Android client communicating seamlessly over structured JSON REST contracts.
