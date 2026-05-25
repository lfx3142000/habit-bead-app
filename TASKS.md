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
- [x] Local persistence prototype using SharedPreferences
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
- [x] Remove legacy SharedPreferences habit/count storage helper after Room wiring

## Current implementation notes

- Room now owns habit and bead count persistence through HabitRepository.
- Existing installs from pre-Room builds may start fresh because SharedPreferences data is not migrated into Room yet.
- Theme choice is currently in-memory only and should be persisted later with preferences.
- MainActivity.kt is small, and HabitBeadsApp.kt is focused on Compose UI, though it can still be split further into component files later.
- Zero-count entries may still be stored as zero rows in Room; they behave visually as empty cells but can be compacted in a later DAO cleanup.

## Next tasks

- [ ] Check latest GitHub Actions build after removing legacy SharedPreferences storage helper
- [ ] Download and install latest APK for manual phone testing
- [ ] Verify Room persistence after close/reopen
- [ ] Persist selected theme choice
- [ ] Add explicit loading state instead of reusing empty state while Room loads
- [ ] Split HabitBeadsApp.kt further into UI component files if needed
- [ ] Add home-screen widget support after core app is stable

## Do not add yet

- Login
- Backend
- Cloud sync
- Analytics
- Billing
