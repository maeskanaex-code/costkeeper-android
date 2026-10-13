# CostKeeper

A modern expense tracker for Android built with Kotlin and Jetpack Compose.

## Features

- Track expenses and income with categories
- Monthly budget with progress tracking
- Analytics dashboard with pie charts and weekly summaries
- Multi-currency support (10 currencies)
- Dark and light themes
- CSV and PDF export
- Multi-language support (English, Russian, Chinese)
- Rewarded ads via Yandex Mobile Ads

## Tech Stack

- **Language:** Kotlin 1.9.24
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM
- **Database:** Room 2.6.1 (KSP)
- **Async:** Coroutines + Flow
- **Build:** Gradle 8.9, AGP 8.7.3
- **Ads:** Yandex Mobile Ads SDK 7.4.0
- **SDK:** minSdk 24, targetSdk 35, compileSdk 35

## Status

Published on Huawei AppGallery (v1.0.3). v2.0 in development.

## Package

`com.mody.costkeeper`

## Build

Requirements: JDK 17+, Android SDK 35

```bash
./gradlew :app:assembleDebug
```
