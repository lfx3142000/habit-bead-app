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
- [x] Local persistence for habits, order, colors, and bead counts using SharedPreferences
- [x] Add habit flow
- [x] Edit habit by tapping habit title row
- [x] Delete habit from the edit dialog with confirmation
- [x] Reorder habits with long-press drag on grip handle
- [x] Remove visible daily counts/targets from main grid
- [x] Reset test data action

## Current implementation notes

- Storage currently uses SharedPreferences for fast prototype validation.
- Room/database migration is intentionally deferred until the UI behavior is stable.
- MainActivity.kt is still a single large file and should be split before major future expansion.

## Next tasks

- [ ] Check latest GitHub Actions build after edit-dialog delete change
- [ ] Download and install latest APK for manual phone testing
- [ ] Gather UI feedback from phone test
- [ ] Split MainActivity.kt into smaller source files
- [ ] Add basic accessibility descriptions for cells and habit controls
- [ ] Add app icon and visual theme polish
- [ ] Replace SharedPreferences with Room entities, DAOs, database, repository, and ViewModel
- [ ] Add home-screen widget support after core app is stable

## Do not add yet

- Login
- Backend
- Cloud sync
- Analytics
- Billing
