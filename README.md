# 🛡️ CyberGuard

### Your Digital Security Companion

CyberGuard is an Android cybersecurity awareness application designed to help users identify potential digital threats, evaluate suspicious content, and develop safer online habits.

The application combines practical security assessment tools with an interactive cybersecurity academy, delivering a modern and user-friendly experience.

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android" alt="Android"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-purple?style=for-the-badge&logo=kotlin" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Backend-Cloudflare%20Workers-orange?style=for-the-badge&logo=cloudflare" alt="Cloudflare Workers"/>
</p>

---

## 🎬 App Demo

Explore CyberGuard and see its features in action.

**▶ Watch the application demo:**

[CyberGuard — Application Demo Video](docs/cyberguard-demo.mp4)

---

## ✨ Key Features

### 🔍 Security Scanning Tools

- **URL Security Scanner:** Evaluates URLs using multiple threat intelligence providers.
- **Password Security Checker:** Evaluates password strength and checks whether passwords may have appeared in known data breaches using the Have I Been Pwned Range API.
- **Wi-Fi Security Scanner:** Examines available network security information and provides security recommendations.
- **QR Code Scanner:** Scans QR codes using the device camera and examines their content.
- **Phishing Message Analyzer:** Analyzes suspicious message text for social engineering indicators, such as urgency, financial requests, suspicious links, login credential requests, and one-time passwords (OTP).

### 🎓 Cybersecurity Academy

An interactive learning experience designed to make cybersecurity awareness easier and more engaging.

- Educational learning paths and awareness articles.
- Interactive quizzes and knowledge assessment.
- Daily cybersecurity challenges.
- Learning progress tracking and experience points (XP).
- Achievements and learning statistics.
- Security tips and notifications.
- Local storage of learning progress on the device.

### 📊 Personal Dashboard

- Overview of security activity.
- Scan results and security alerts.
- Scan history.
- User profile and learning progress.
- Arabic and English language support.
- Modern user interface inspired by the cybersecurity field.

---

## 🧠 Threat Intelligence

CyberGuard uses a dedicated Cloudflare Worker service to process URL scanning requests and communicate with threat intelligence providers.

| Provider | Purpose |
|---|---|
| Google Safe Browsing | Checks URLs against known threat databases. |
| URLhaus | Identifies URLs associated with malware distribution. |
| VirusTotal | Retrieves available URL analysis results from multiple security engines. |

### How It Works

1. The user enters a URL in the application.
2. The application sends the scan request to the CyberGuard Worker service.
3. The service validates the submitted URL.
4. The service communicates independently and concurrently with threat intelligence providers.
5. The application receives the available results from the providers.

Scanning operations are performed independently. Therefore, a failure or timeout from one provider does not automatically cancel requests to the other providers.

### Security Measures

- Store API keys as secrets inside Cloudflare Worker.
- Validate URLs and reject private or internal network addresses.
- Validate requests and limit payload sizes.
- Apply timeout limits to provider responses.
- Use Cloudflare Rate Limiting to control request rates.
- Never include provider API keys in the Android application source code.

---

## 🛠️ Technologies Used

### Android Application

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | User interface development |
| Material 3 | UI design system |
| Room | Local database |
| OkHttp | HTTP communication |
| Kotlin Coroutines | Asynchronous operations |
| Google ML Kit | Barcode and QR code scanning |
| ZXing | QR code and barcode processing |
| Coil | Image loading |
| Gradle | Project build system |

### Backend and Infrastructure

| Technology | Purpose |
|---|---|
| Cloudflare Workers | Backend service execution |
| JavaScript | Backend logic |
| Wrangler | Cloudflare Worker configuration and deployment |
| Google Safe Browsing | URL threat assessment |
| URLhaus | Malware-related URL intelligence |
| VirusTotal | URL analysis using multiple engines |
| Have I Been Pwned | Password breach checking |

---

## 📱 Requirements

| Requirement | Value |
|---|---|
| Platform | Android |
| Minimum Android Version | Android 7.0 (API 24) |
| Compile SDK | API 36 |
| Target SDK | API 36 |
| Application Version | 1.0.0 |

Some features require runtime permissions, such as camera access for QR code scanning and location access for Wi-Fi network scanning on supported Android versions.

---

## 🚀 Getting Started

### 1. Obtain the Project Files

Download or obtain a copy of the project files and extract the archive if they are provided in a ZIP file.

### 2. Open the Project

1. Open Android Studio.
2. Select **Open**.
3. Navigate to the `CyberGuard` directory.
4. Wait for Gradle synchronization to complete.
5. Run the `app` configuration on an emulator or a physical Android device.

### 3. Build a Debug APK

**Windows PowerShell:**

```powershell
.\gradlew.bat :app:assembleDebug
```

**Linux / macOS:**

```bash
./gradlew :app:assembleDebug
```

The generated APK will be available at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### 4. Run Unit Tests

**Windows PowerShell:**

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

**Linux / macOS:**

```bash
./gradlew :app:testDebugUnitTest
```

### 5. Run Android Lint

```bash
./gradlew :app:lintDebug
```

---

## ☁️ Cloudflare Worker Setup

The URL scanning feature requires deploying a Cloudflare Worker and configuring the provider credentials and Rate Limiting system.

The deployment configuration files are located in the project root directory:

```text
worker.js
wrangler.toml
```

### Required Service Secrets

Configure the following secrets in your Cloudflare Worker:

```text
GOOGLE_SAFE_BROWSING_API_KEY
URLHAUS_AUTH_KEY
VIRUSTOTAL_API_KEY
```

Never upload these credentials to GitHub or include them in the Android application.

### Deploy the Worker

From the project root directory, run:

```bash
npx wrangler@latest login
npx wrangler@latest deploy --config wrangler.toml
```

The `wrangler.toml` file includes the `RATE_LIMITER` binding configuration.

For complete deployment instructions, configuration details, and verification steps, refer to:

📖 **Cloudflare Worker Deployment Guide (Arabic)**

---

## 🔐 Security and Privacy

CyberGuard follows security-conscious development practices when handling data and external services.

- Provider API keys are configured as secrets inside Cloudflare Worker.
- Submitted URLs are sent to the CyberGuard Worker service and its configured threat intelligence providers for evaluation.
- Password breach checking uses the Have I Been Pwned Range API, with the SHA-1 hash prefix calculated locally on the device.
- The Worker service validates submitted URLs and rejects private or internal network addresses.
- Application and learning data are stored locally using the application's local storage layer.

For more information about data handling, please review the **Privacy Policy and Terms** inside the application.

> **Disclaimer:** CyberGuard provides cybersecurity awareness information and security assessment results. Scan results should not be considered a guarantee that a website, password, QR code, or network is completely safe.

Never upload API keys, signing credentials, Keystore files, or private user data to this repository.

---

## 📂 Project Structure

```text
CyberGuard/
│
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   ├── res/
│       │   └── assets/
│       │
│       ├── test/
│       └── androidTest/
│
├── cloudflare-worker/
│   ├── index.js
│   ├── README_AR.md
│   └── SECURITY_NOTES_AR.md
│
├── worker.js
├── wrangler.toml
├── gradle/
├── gradlew
├── gradlew.bat
└── README.md
```

---

## 🧪 Testing

The project includes unit tests and Android instrumentation tests using tools such as:

- JUnit
- Robolectric
- AndroidX Test
- Compose UI Testing
- Kotlin Coroutines Test

---

## 👨‍💻 About

CyberGuard is a project that aims to combine practical security assessment tools with interactive learning to promote digital security awareness.

---

## ⭐ Support the Project

We welcome contributions, suggestions, and feedback that can help improve CyberGuard and its user experience.

---

<p align="center">
  <b>CyberGuard — Stay Aware. Stay Protected. 🛡️</b>
</p>