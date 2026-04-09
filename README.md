# 🔧 ToolShare — Android App

A community tool-sharing app built with Kotlin + Firebase for a Mobile Application Development course project.

---

## 📱 Features

| Feature | Status |
|---|---|
| User Registration & Login | ✅ Firebase Auth |
| Browse Available Tools | ✅ Firestore |
| Search Tools | ✅ |
| List Your Own Tools | ✅ |
| Request to Borrow | ✅ |
| View & Manage Requests | ✅ |
| User Profile | ✅ |

---

## 🚀 Setup Instructions

### Step 1 — Open in Android Studio
1. Open Android Studio → **File > Open** → select the `ToolShareApp` folder

### Step 2 — Set Up Firebase
1. Go to [https://console.firebase.google.com](https://console.firebase.google.com)
2. Click **Add Project** → name it "ToolShare"
3. Inside the project, click **Add App** → select **Android**
4. Enter package name: `com.example.toolshare`
5. Download the `google-services.json` file
6. Place it in: `ToolShareApp/app/google-services.json`

### Step 3 — Enable Firebase Services
In the Firebase Console:
- **Authentication** → Sign-in methods → Enable **Email/Password**
- **Firestore Database** → Create database → Start in **test mode**

### Step 4 — Firestore Security Rules (for dev/testing)
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### Step 5 — Sync & Run
1. Click **Sync Now** when Android Studio prompts for Gradle sync
2. Connect a device or start an emulator (API 24+)
3. Click ▶ Run

---

## 📁 Project Structure

```
app/src/main/
├── java/com/example/toolshare/
│   ├── activities/
│   │   ├── SplashActivity.kt       ← Launch screen, checks auth
│   │   ├── LoginActivity.kt        ← Email/password login
│   │   ├── RegisterActivity.kt     ← New user registration
│   │   ├── MainActivity.kt         ← Home: Browse/My Tools/Requests tabs
│   │   ├── AddToolActivity.kt      ← List a new tool
│   │   ├── ToolDetailActivity.kt   ← Tool info + borrow request
│   │   └── ProfileActivity.kt      ← Edit profile + logout
│   ├── adapters/
│   │   └── ToolAdapter.kt          ← RecyclerView adapter for tool list
│   ├── models/
│   │   ├── Tool.kt                 ← Tool data class
│   │   ├── BorrowRequest.kt        ← Borrow request data class
│   │   └── User.kt                 ← User profile data class
│   └── utils/
│       └── FirebaseHelper.kt       ← All Firebase operations (auth, db)
└── res/
    ├── layout/                     ← All XML layouts
    ├── drawable/                   ← Shapes and backgrounds
    ├── menu/                       ← Bottom nav menu
    └── values/                     ← colors, strings, themes
```

---

## 🗄️ Firestore Collections

| Collection | Document Fields |
|---|---|
| `users` | uid, name, email, phone, location, toolsListed, toolsBorrowed |
| `tools` | id, name, description, category, condition, ownerId, ownerName, isAvailable, location |
| `requests` | id, toolId, toolName, requesterId, requesterName, ownerId, message, status |

---

## 🔮 Possible Enhancements

- Tool photos via Firebase Storage + Glide
- Push notifications for request approvals
- In-app chat between borrower and owner
- Google Maps integration for nearby tools
- Ratings/reviews system

---

## 🛠️ Built With

- Kotlin
- Firebase Auth + Firestore
- Material Design Components
- RecyclerView + ViewBinding
- Kotlin Coroutines
