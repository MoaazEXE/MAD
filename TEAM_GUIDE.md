# ByteLift - Page Development Guide

## Pages Structure

The app now has the following pages accessible from the navigation drawer:

1. **Profile** (existing)
2. **Home**
3. **Leaderboard**
4. **Workout**
5. **Diet**
6. **Settings**

Plus **Logout** button at the bottom of the drawer (red text).

---

## Files to Edit for Each Page

### 🏠 HOME PAGE

**Assigned Team Member:** ******\_******

**Files to edit:**

- `app/src/main/java/com/example/bytelift/ui/home/HomeFragment.java`
- `app/src/main/java/com/example/bytelift/ui/home/HomeViewModel.java`
- `app/src/main/res/layout/fragment_home.xml`

---

### 🏆 LEADERBOARD PAGE

**Assigned Team Member:** ******\_******

**Files to edit:**

- `app/src/main/java/com/example/bytelift/ui/leaderboard/LeaderboardFragment.java`
- `app/src/main/java/com/example/bytelift/ui/leaderboard/LeaderboardViewModel.java`
- `app/src/main/res/layout/fragment_leaderboard.xml`

---

### 💪 WORKOUT PAGE

**Assigned Team Member:** ******\_******

**Files to edit:**

- `app/src/main/java/com/example/bytelift/ui/workout/WorkoutFragment.java`
- `app/src/main/java/com/example/bytelift/ui/workout/WorkoutViewModel.java`
- `app/src/main/res/layout/fragment_workout.xml`

---

### 🥗 DIET PAGE

**Assigned Team Member:** ******\_******

**Files to edit:**

- `app/src/main/java/com/example/bytelift/ui/diet/DietFragment.java`
- `app/src/main/java/com/example/bytelift/ui/diet/DietViewModel.java`
- `app/src/main/res/layout/fragment_diet.xml`

---

### 👤 PROFILE PAGE

**Assigned Team Member:** ******\_******

**Files to edit:**

- `app/src/main/java/com/example/bytelift/ui/profile/ProfileFragment.java`
- `app/src/main/java/com/example/bytelift/ui/profile/ProfileViewModel.java`
- `app/src/main/res/layout/fragment_profile.xml`

---

### ⚙️ SETTINGS PAGE

**Assigned Team Member:** ******\_******

**Files to edit:**

- `app/src/main/java/com/example/bytelift/ui/settings_page/SettingsFragment.java`
- `app/src/main/java/com/example/bytelift/ui/settings_page/SettingsViewModel.java`
- `app/src/main/res/layout/fragment_settings.xml`

---

## Important Notes for Team

### ✅ DO:

- Only edit the 3 files assigned to your page
- Use the app's theme colors (no hardcoded colors)
- Test your page before pushing
- Create a new branch for your work: `git checkout -b feature/your-page-name`
- Commit frequently with clear messages

### ❌ DON'T:

- Edit navigation files (`mobile_navigation.xml`, `activity_main_drawer.xml`)
- Edit MainActivity.java
- Edit other team members' page files
- Hardcode colors or strings (use `colors.xml` and `strings.xml`)
- Delete any files

### 🔀 When You're Ready to Merge:

1. Pull latest changes: `git pull origin main`
2. Resolve any conflicts (should be minimal if you only edited your assigned files)
3. Push your branch: `git push origin feature/your-page-name`
4. Create a Pull Request on GitHub

---

## Navigation Structure

The drawer contains:

- Profile (no icon, text only)
- Home (no icon, text only)
- Leaderboard (no icon, text only)
- Workout (no icon, text only)
- Diet (no icon, text only)
- Settings (gear icon)
- **Logout** (red text, at bottom)

The 3-dot menu has been removed. All navigation is through the drawer.

---

## Firebase Integration

The app has Firebase Authentication with:

- Email/Password login
- Google Sign-In
- Firestore database (ready to use)

Access Firebase in your Fragment:

```java
FirebaseAuth auth = FirebaseAuth.getInstance();
FirebaseFirestore db = FirebaseFirestore.getInstance();
```

---

## Theme Colors Available

Use these in your XML layouts:

- `?attr/colorPrimary` - Green
- `?attr/colorOnBackground` - White/Black (auto-adjusts)
- `?attr/colorOnPrimary` - White
- `@color/green_500`, `@color/green_700`
- `@color/red` (for errors/warnings)
