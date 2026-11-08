# CLEAN Stretch — Claude Code entry point

@AGENTS.md

Start every session by reading docs/CONTINUE.md. Build, test and lint with:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
```

Run Android tests only against an emulator (never a personal phone); never clear app data.
