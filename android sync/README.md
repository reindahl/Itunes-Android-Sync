# Android Sync

Android Sync reads an iTunes library XML, prepares hardlinked media files, and syncs them to Android devices over UMS or MTP.

## Settings file
The application reads and writes a file named `Settings` in the project working directory.

Example:

```
Device name = My Android
Itunes Path = I:/iTunes/
Itunes Sync path = I:/Android Sync/
Excluded media types = audiobooks,podcasts
```

The `Excluded media types` setting is a comma-separated list of media categories that should be ignored during source preparation and therefore never added to the prepared sync set.

This is evaluated at the iTunes/hardlink preparation stage, before the device transport layer sees the work.

## Media type examples
Common values used by the app are normalized to lower-case, for example:
- `music`
- `audiobooks`
- `podcasts`

If a media type is excluded, its files are not created as hardlinks and are not considered part of the sync.
