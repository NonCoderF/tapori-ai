# Tapori AI — React Native

This directory is the React Native implementation on the `react-native` branch. The original root Android/Compose implementation is intentionally left in place.

## Architecture

The app is feature-oriented: `features/auth`, `features/chat`, and `features/credits` own user-facing behavior; `services` owns API, storage, configuration, and native wrappers; `navigation` owns stack composition; and `types` contains transport/domain models. Chat business rules are isolated in a reducer and `useChat`, keeping components focused on rendering and input events.

The app uses React Native 0.78.1 with TypeScript strict mode. React Navigation native stack provides native-backed navigation. AsyncStorage stores the existing session/chat identifiers; production hardening should replace token storage with an encrypted credential store before release.

## Running locally

Requirements: Node 20+, JDK 17/21, Android SDK 35, and an Android device/emulator. From this directory:

```powershell
npm install
$env:TAPORI_GOOGLE_WEB_CLIENT_ID = 'your-web-client-id.apps.googleusercontent.com'
npm run typecheck
npm run lint
npm test
cd android
./gradlew.bat assembleDebug
./gradlew.bat assembleRelease
```

Google sign-in requires a matching OAuth web client ID and Android OAuth client. No client ID, keystore, Razorpay key, or API secret is committed. Configure the client ID in the bundler/build environment before using sign-in.

## Backend and networking

The client preserves the existing Supabase Edge Function base URL and payload shapes: `POST chat`, `POST download`, and `POST add_credits`. `src/services/api.ts` centralizes JSON requests, 15-second timeout, cancellation, network error mapping, and HTTP status mapping. 402 is deliberately retained as a domain state so the chat UI can show the server's `reply` and direct the user to credits.

## Kotlin ↔ React Native bridge

`TaporiDeviceModule` exposes Android manufacturer/model/SDK diagnostics through `NativeModules.TaporiDevice.getDeviceInfo()`. `TaporiAuthModule` owns the Android Google Sign-In activity flow and returns only the ID token/display name to TypeScript; activity results are resolved/rejected through a Promise and pending work is rejected if the module is invalidated. The host currently keeps the legacy module API (`newArchEnabled=false`) for a stable RN 0.78 integration; the module is isolated behind a wrapper for incremental TurboModule migration.

## Testing and CI

Reducer tests cover send transitions, 401/408 mapping, and 402 credit behavior. API tests use realistic JSON/HTTP responses. Auth restoration and the native bridge wrapper have component/unit tests. Run `npm test` for all Jest tests. `.github/workflows/react-native.yml` runs install, typecheck, lint, tests, and debug/release Android builds.

## Native Android vs React Native

Compose renders Kotlin UI directly in the Android process; React Native renders JavaScript-defined components through the RN runtime and native views. Compose state is commonly held in ViewModels/Flows, while this client uses React context plus reducer/hook state. Compose Navigation and React Navigation both model route stacks, but their lifecycle integration differs. Kotlin Retrofit/coroutines and RN `fetch`/Promises have different cancellation and threading semantics; RN JS work runs on the JS runtime and native modules cross the bridge. Native Android gives direct platform API access; RN keeps that access behind Kotlin modules. Compose UI tests/Espresso and Jest/RNTL exercise different layers. Android Gradle release signing/minification remains native in both cases, while RN also needs JS bundling and Metro dependency reproducibility. Large lists, bridge crossings, and unnecessary React renders need explicit attention in RN.

## Known porting gaps and risks

The existing Razorpay checkout activity is not yet ported; the credit screen intentionally does not mutate credits until a verified payment result exists. Voice input/text-to-speech is also not ported. AsyncStorage is functional but not appropriate for long-lived ID tokens without encryption. Google configuration and release signing must be supplied by the deployer. End-to-end verification against the live backend requires valid Google credentials and network access.
