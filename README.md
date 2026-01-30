# V-Fresher 2026
## Double Decode Key
This project is an Android application built with **Kotlin** and **Jetpack Compose** to solve the **Double Decode Key** practical task.

The app decodes a Base64URL-encoded input string, follows the decoded instruction, and performs the required decoding steps to obtain the final KEY.


## Development & Test Environment

- **Emulator**: Pixel 5a API 33
- **Kotlin**: 1.9.0
- **Compile SDK**: 35
- **Min SDK**: 33
- **Target SDK**: 35
- **Android Version**: Android 13 (API 33)

## Project Structure & Versions

This repository contains **two branches**, each representing a different usage scenario of the application.

### 1️⃣ `main` branch — One-click decode (Result-focused)

- Automatically performs **all decoding steps** in one execution.
- Suitable if you only need the **final decoded KEY**.
- Minimal UI and logic, focused on correctness of the result.

**Use this branch if**:
- You want a quick verification of the decoding logic.
- You only care about the final output.

---

### 2️⃣ `dev/decoder-controls` branch — Step-by-step interactive decode (Recommended)

- Provides **interactive controls** to execute decoding **step by step**:
  - Decode instruction
  - Decode cipher text
  - Caesar decode to get KEY
- Includes:
  - Reset functionality
  - Error handling per step
  - Clear state transitions via ViewModel

**Use this branch if**:
- You want to observe and validate each decoding step.
- You want better control, debuggability, and UX.
- You are reviewing the implementation logic in detail.

👉 **Recommended branch for evaluation and review.**


## Decoding Flow Overview

The decoding process follows these steps:

1. **Base64URL decode** the input string to obtain the instruction.
2. From the instruction, decode the embedded Base64URL string to get the cipher text.
3. **Caesar decode** the cipher text with a fixed shift to obtain the final KEY.

Each step is implemented as a pure function and orchestrated via a `ViewModel` to ensure:
- Clear state management
- UI consistency
- Easy reset and re-run capability


## Architecture Notes

- **UI**: Jetpack Compose (state-driven)
- **State Management**: `ViewModel` + `mutableStateOf`
- **Business Logic**:
  - Base64URL decoding
  - Caesar cipher decoding
  - Step-based state machine (`DecodeStep`)

The decoding logic is isolated from UI components to keep the codebase clean and testable.


## Notes

- The application is tested on **Android 13 (API 33)** emulator.
- All decoding operations are deterministic and do not require network access.
- No external libraries are used for decoding logic to ensure transparency and simplicity.