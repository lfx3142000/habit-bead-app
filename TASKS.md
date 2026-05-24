# Habit Beads TASKS

## Current status

Habit Beads is now a working Android/Compose prototype on the `ci-validate-build` PR branch. The app builds through GitHub Actions and produces a downloadable debug APK artifact.

## Completed

- [x] Basic Android project structure
- [x] Kotlin/Compose setup
- [x] Landscape-only MainActivity
- [x] GitHub Actions debug APK workflow
- [x] Confirm GitHub Actions build passes
- [x] 14-day bead tracker grid
- [x] Square bead cells
- [x] Tap bead cell to increment count, up to 9
- [x] Long-press bead cell to decrement count
- [x] Local persistence for habits, order, colors, subtitles, and bead counts using SharedPreferences
- [x] Add habit flow
- [x] Edit habit by tapping habit title row
- [x] Add optional subtitle field for habits
- [x] Add longer default sample habit title/subtitle for layout testing
- [x] Delete habit from the edit dialog with confirmation
- [x] Reorder habits with long-press drag on grip handle
- [x] Remove visible daily counts/targets from main grid
- [x] Reset test data action
- [x] Move tracker UI out of MainActivity.kt into HabitBeadsApp.kt
- [x] Reduce MainActivity.kt to app entry point only
- [x] Split models/constants into Models.kt
- [x] Split date helper into DateHelpers.kt
- [x] Split SharedPreferences storage into HabitStorage.kt
- [x] Add basic accessibility descriptions for habit rows, drag grips, day headers, bead cells, and color choices

## Current implementation notes

- Storage currently uses SharedPreferences for fast prototype validation.
- Habit storage supports the old 4-field saved format and the new 5-field title/subtitle format.
- Room/database migration is intentionally deferred until the UI behavior is stable.
- MainActivity.kt is now small, and HabitBeadsApp.kt is now focused on Compose UI, though it can still be split further into component files later.

## Next tasks

- [ ] Check latest GitHub Actions build after accessibility labels
- [ ] Download and install latest APK for manual phone testing
- [ ] Gather UI feedback from phone test
- [ ] Split HabitBeadsApp.kt further into UI component files if needed
- [ ] Add app icon and visual theme polish
- [ ] Replace SharedPreferences with Room entities, DAOs, database, repository, and ViewModel
- [ ] Add home-screen widget support after core app is stable

## Do not add yet

- Login
- Backend
- Cloud sync
- Analytics
- Billing
