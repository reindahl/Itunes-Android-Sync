# Copilot instructions for Android Sync

## Project overview
This project is a Java desktop app that reads an iTunes library XML, prepares hardlinked media files, and syncs them to Android devices over UMS or MTP.

Key packages:
- `driver/` : entry point and orchestration
- `settings/` : persisted application settings
- `findFiles/` : iTunes parsing, track normalization, hardlink creation
- `syncFiles/` : device transport logic
- `syncFiles/UMS/` and `syncFiles/MTP/` : device-specific implementations

## Architectural rules
- Keep device implementations low-level and transport-focused.
- Do not put media filtering or sync-policy logic in `Device`, `DeviceUMS`, or `DeviceMTP`.
- Filter excluded media types before the device layer sees the work, during source preparation in `findFiles.Find`.
- Keep the rules in `settings.Settings` as app-level configuration, not device code.
- Prefer minimal, localized changes. Avoid broad refactors unless required for the feature.

## Settings conventions
- Settings are stored in a file named `Settings` in the project working directory.
- Read and write settings through `Settings.readSettings()` and `Settings.writeSettings()`.
- New configuration should be added to `settings.Settings` and use the existing `Properties` pattern.
- Use lower-case normalized values when comparing media types (`audiobooks`, `podcasts`, etc.).
- Keep property names stable and human-readable.

## Sync behavior rules
- Media-type exclusion is a source-preparation concern, not a device concern.
- If a media type is excluded, it should never be added to the prepared sync set.
- Do not create hardlinks for excluded media types.
- Do not let device code decide policy for excluded categories.

## Coding style
- Prefer modern Java where it is simple and safe, but keep the project’s existing package layout and runtime compatibility.
- Use clear, readable imperative Java and keep methods small and focused.
- Favor straightforward language features over unnecessary abstraction, but do not avoid modern syntax that improves correctness and readability.
- Keep naming consistent with the existing codebase and preserve public method names and integration points unless required by the fix.
- Avoid adding frameworks or heavy dependency changes for small features.

## Testing requirements
- Every task is not complete until the relevant code is validated.
- Run the compiler/error checker on affected Java files after making changes.
- Prefer a focused validation pass over broad cleanup.
- For logic changes, validate the behavior against both the UMS and MTP code paths when applicable.
- If a fix changes sync behavior, ensure the resulting path is still compatible with the existing source-preparation flow and not device-specific policy.
- Add or update focused tests when practical, especially for parsing, filtering, and sync-path behavior.
- Prefer higher-level tests that exercise the real application flow over low-level unit tests when the behavior is integration-focused.
- Use end-to-end tests around `Find.FindFiles()`, sync preparation, and the device-facing flow when validating media filtering or sync policy.
- Keep tests close to the behavior they validate; for source-preparation rules, test the prepared hardlink set and the file tree, not just individual helper methods.

## Definition of done
- The change is implemented in the correct architectural layer.
- The settings behavior is documented and persisted through `Settings.readSettings()` / `Settings.writeSettings()` when appropriate.
- The relevant validation step has been run successfully.
- The task is only considered complete after the validation result is checked and any issues are resolved.

## Example exclusion config
Use a comma-separated list in the `Settings` file, for example:
- `Excluded media types = audiobooks`
- `Excluded media types = audiobooks,podcasts`

This should be interpreted at the iTunes/hardlink preparation stage, not in the device layer.