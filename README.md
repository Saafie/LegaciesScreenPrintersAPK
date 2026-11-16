
# Legacies Screen Printers – Mobile Ordering App

Video Demonstration (YouTube, unlisted): https://youtu.be/yL6uNkRqnlo?si=manciL88r3y1n0dE

A native **Kotlin Android application** built for **Legacies Screen Printers**. Customers use it to place print orders, track their progress through production, and manage their account. The app talks to a **C# ASP.NET Core Web API** hosted on **Azure App Service**, which stores everything in an **Azure SQL Database** shared with the Legacies website.

This project was built to replace the business's paper-based order process with a reliable client-server system, so that orders, stock and progress updates live in one place.

---

## 🚀 Key Features
* **Sign In & Registration:** Customers register with their name, contact number, email and password. Every field is checked before it is sent, and passwords are hashed with BCrypt by the API.
* **Google Single Sign-On:** "Sign in with Google" through Android Credential Manager. The API verifies the Google token, so the same account works on the app and the website.
* **Secure Sessions:** A JWT token is stored after sign-in and added to every request. If it expires or becomes invalid, the user is sent back to sign in with a clear message.
* **Order Placement:** Add several designs to one order, pick a vinyl from the live price list and set a quantity. The total updates as you go, and stock is deducted when the order is placed.
* **Live Order Tracking:** Enter an order reference to see a four-stage timeline: Order Received, Vinyl Cutting, Screen Printing and Ready for Collection.
* **Order History & Offline Mode:** Past orders load from the API. With no connection, the app shows the last saved copy and says clearly that it is offline.
* **Settings, Light/Dark Mode & Help:** Update profile details, switch the whole app between light and dark themes, and read the built-in user guide and FAQ.

---

## 📝 Release Notes

### Version 2.0.0 – Final Release
This version turns the prototype (version 1.0), which ran on mock data, into a fully working app connected to a live cloud backend and prepared for Google Play.

**⭐ Innovative features added in this version**
* **One account across the app and the website:** Google single sign-on now uses a real Google client and is verified by the API, so customers use the same account on both platforms.
* **Live production tracking synced with the workshop:** The four-stage timeline now shows real data. When staff move an order to the next stage on the website, the customer sees the update in the app.
* **Offline mode:** The app saves the customer's latest orders and shows them with a clear offline message when there is no connection.
* **Automatic pricing and stock control:** Vinyl prices load live from the database, totals are calculated as the customer builds the order, and stock is deducted automatically when the order is placed.
* **Full light and dark themes:** Every screen now follows the theme switch in Settings, not just the background.

**Other updates since the prototype**
* Replaced the mock repositories with a live connection to the ASP.NET Core API hosted on Azure App Service and Azure SQL Database.
* Added secure sessions: JWT tokens are added to every request by an auth interceptor, and expired or invalid sessions send the user back to sign in with a clear message.
* Registration and order placement now save to the database, and order history loads from the API.
* Added a network security config so the app only uses HTTPS.
* Prepared for Google Play: permanent app ID (`za.co.legaciesscreenprinters.app`), version 2.0.0, R8 code shrinking, release signing, Play Store icon and feature graphic, and privacy policy and account deletion pages on the website.
* Replaced the example tests with real unit tests for JWT handling, the auth interceptor, the order repository, stage mapping, dates and security helpers.
* GitHub Actions now runs lint, unit tests and a debug build on every push, and builds a signed release on version tags.
* Fixed light-mode text and colours on the login, register, home, order and tracking screens.

---

## 🛠️ Technology Stack & Architecture
* **Mobile:** Kotlin, Jetpack Compose, Material Design 3, Retrofit and OkHttp (REST client), Android Credential Manager (Google sign-in)
* **Backend:** C#, ASP.NET Core Web API (.NET 10), Entity Framework Core, JWT authentication, BCrypt password hashing ([API repo](https://github.com/Ethan1ol/LegaciesScreenPrintersAPI))
* **Database:** Azure SQL Database (relational design shared with the website)
* **Cloud & DevOps:** Azure App Service, GitHub Actions (lint, unit tests and builds on every push)
* **Related:** Customer and staff website ([website repo](https://github.com/Ethan1ol/TheDominantDuo_XISD6329), [live site](https://ethan1ol.github.io/TheDominantDuo_XISD6329/))

---

## 📂 Project Structure
```text
LegaciesScreenPrintersAPK/
│
├── app/src/main/java/com/example/legaciesscreenprintersapk/
│   ├── data/                 # App data models
│   ├── ui/
│   │   ├── auth/             # Login, registration and sign-in repository
│   │   ├── home/             # Customer portal home, order history and help
│   │   ├── main/             # Bottom navigation between the main screens
│   │   ├── order/            # Order placement
│   │   ├── track/            # Order tracking timeline
│   │   ├── settings/         # Profile, theme switch and help guide
│   │   ├── remote/           # Retrofit client, API service, auth interceptor and network models
│   │   └── theme/            # Light and dark colour schemes
│   ├── util/                 # Session manager, JWT, Google sign-in, security and date helpers
│   └── MainActivity.kt       # App entry point and navigation host
│
├── app/src/test/             # Unit tests (JWT, auth interceptor, orders, stages, dates, security)
├── docs/play-store/          # Google Play icon and feature graphic
└── .github/workflows/        # Android CI pipeline
```

---

## 📱 Screenshots

| Login | Register | Home (dark) | Home (light) |
|---|---|---|---|
| ![Login](docs/playstore/screemshots/screenshots/01-login.png) | ![Register](docs/screenshots/02-register.png) | ![Home](docs/screenshots/06-home.png) | ![Home light](docs/screenshots/23-home-light.png) |

| Place Order | Order Confirmation | Tracking Timeline | Offline Order History |
|---|---|---|---|
| ![Place order](docs/screenshots/11-order-two-designs.png) | ![Confirmation](docs/screenshots/12-order-confirmation.png) | ![Tracking](docs/screenshots/13-track-timeline.png) | ![Offline](docs/screenshots/17-offline-order-history.png) |

| Settings | Google Sign-In |
|---|---|
| ![Settings](docs/screenshots/20-settings-dark.png) | ![Google sign-in](docs/screenshots/24-google-no-account.png) |

---

## 📦 Preparation for Publication
* **Signed release:** Built with `./gradlew assembleRelease bundleRelease` using the upload key in `keystore.properties`. The signed APK and App Bundle are attached to the GitHub release.
* **Store listing assets:** App icon and feature graphic in [`docs/play-store/`](docs/play-store), and app screenshots in [`docs/screenshots/`](docs/screenshots).
* **Policies:** [Privacy policy](https://ethan1ol.github.io/TheDominantDuo_XISD6329/privacy.html) and [account deletion](https://ethan1ol.github.io/TheDominantDuo_XISD6329/delete-account.html) pages, as required by Google Play.
* **Google Play Console:** ![App uploaded on Google Play Console](docs/screenshots/play-console.png)

---

## ⚙️ Setup & Local Installation

### 1. Running the Android Application
1. Open the project folder in **Android Studio**.
2. Sync Gradle and run the app on an Android Emulator or physical device.
3. By default every build uses the hosted Azure API, so no backend setup is needed.

### 2. Using a Local API (optional)
1. Clone and run the [API project](https://github.com/Ethan1ol/LegaciesScreenPrintersAPI) on your machine.
2. Add the local address to `local.properties` (debug builds only):
   ```properties
   apiBaseUrl=http://10.0.2.2:5000/
   ```
   `10.0.2.2` points to your computer from the Android Emulator.
3. Sync Gradle and run the app again.

### 3. Running the Tests
```bash
./gradlew testDebugUnitTest
```

### 4. Building a Signed Release
1. Copy `keystore.properties.example` to `keystore.properties` and fill in your own keystore details (this file is ignored by Git).
2. Build the release bundle for Google Play:
   ```bash
   ./gradlew bundleRelease
   ```

---

## 🔄 Development & Collaboration Workflow
* Managed with Git branches for feature development and bug fixes.
* GitHub Actions runs lint, unit tests and a debug build on every push and pull request, and builds a signed release on version tags.
* Clear separation between UI screens, repositories and the network layer, communicating with the API over structured JSON REST contracts.
