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
- [x] Add empty state when all habits are deleted
- [x] Move reset action into Options dialog to reduce main tracker clutter
- [x] Improve top bar balance by keeping primary action focused on Add habit
- [x] Add simple Habit Beads launcher icon resources
- [x] Add branded Compose color scheme
- [x] Compact grid layout based on phone screenshot feedback
- [x] Add status bar safe top padding so the title is not cut off
- [x] Add selectable color themes in Options: Warm, Ocean, Forest, and Grape
- [x] Add Room dependencies and KSP
- [x] Add Room entities, DAOs, database, provider, and repository
- [x] Wire tracker UI to Room-backed repository for habits and bead counts

## Current implementation notes

- Habit and bead count storage is being migrated from SharedPreferences to Room.
- Room now owns new habit/count persistence through the repository.
- Existing SharedPreferences helper files remain in the repo for now but are no longer the intended storage path for the tracker UI.
- Theme choice is currently in-memory only and should be persisted later with preferences.
- MainActivity.kt is now small, and HabitBeadsApp.kt is focused on Compose UI, though it can still be split further into component files later.

## Next tasks

- [ ] Check latest GitHub Actions build after Room migration wiring
- [ ] Fix any Room/KSP compile issues
- [ ] Download and install latest APK for manual phone testing
- [ ] Verify Room persistence after close/reopen
- [ ] Persist selected theme choice
- [ ] Split HabitBeadsApp.kt further into UI component files if needed
- [ ] Add home-screen widget support after core app is stable

## Do not add yet

- Login
- Backend
- Cloud sync
- Analytics
- Billing
