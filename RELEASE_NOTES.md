# MindScale - Release Notes & Pre-Submission Guide

This document outlines instructions for generating production signing keys, building signed release artifacts (`.aab` / `.apk`), and setting up Google Play Console store listings.

---

## 1. Keytool Keystore Generation (One-Time Setup)

Run the following command in your terminal to generate a secure 2048-bit RSA release keystore:

```bash
keytool -genkey -v -keystore release.keystore -alias mindscale_key -keyalg RSA -keysize 2048 -validity 10000
```

---

## 2. Signing Configuration

1. Copy `keystore.properties.example` to `keystore.properties` in the project root directory:

```bash
cp keystore.properties.example keystore.properties
```

2. Open `keystore.properties` and fill in your generated keystore path and passwords:

```properties
storeFile=release.keystore
storePassword=YOUR_ACTUAL_STORE_PASSWORD
keyAlias=mindscale_key
keyPassword=YOUR_ACTUAL_KEY_PASSWORD
```

*Note: `keystore.properties` and `*.keystore` / `*.jks` files are ignored by `.gitignore` and will never be committed to source control.*

---

## 3. Building Production Release Artifacts

To compile and package the release artifact with R8 code minification and resource shrinking:

- **Android App Bundle (.aab)** (Recommended for Google Play Store):
  ```bash
  ./gradlew bundleRelease
  ```
  *Output Path:* `app/build/outputs/bundle/release/app-release.aab`

- **Signed Release APK (.apk)** (Direct device installation):
  ```bash
  ./gradlew assembleRelease
  ```
  *Output Path:* `app/build/outputs/apk/release/app-release.apk`

---

## 4. Google Play Console Listing Details

- **Application ID:** `com.mindscale.games`
- **Version Code:** `1`
- **Version Name:** `1.0.0`
- **Target SDK:** `36`
- **Min SDK:** `24`

### Short Description (≤ 80 characters)
> Compare math expressions, test your speed, and scale your mental math streak!

### Full Description
> **MindScale** is a sleek, fast-paced math comparison game designed to sharpen your mental math agility.
>
> **Key Features:**
> - **Classic Mode:** Endless practice with zero time pressure. Relax and calculate at your own pace.
> - **Arcade Mode:** 3-lives challenge with per-round countdown timers, streak multipliers, and high-intensity gameplay!
> - **In-Game Scratch Pad:** Quick on-screen scratchpad to work out calculations by hand.
> - **Live Performance Analytics:** Track your accuracy percentages by difficulty (Easy, Medium, Hard) and monitor your best streaks over time.
> - **Customizable Audio & Haptics:** Seamless sound effects and tactile haptic feedback with individual preference controls.

### Privacy Policy Declaration
> **MindScale Privacy Statement:**
> MindScale utilizes **Firebase Analytics** for anonymous gameplay analytics (screen views, game start/end events, accuracy stats). No personally identifiable information (PII) is collected or shared. All personal game progress and local settings remain securely stored on device using Android DataStore and Room Database.
