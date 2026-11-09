# Windows version of build-and-run.sh. Run from the repo root in PowerShell:
#   .\tools\pose-preview\build-and-run.ps1                 # every stretch
#   .\tools\pose-preview\build-and-run.ps1 butterfly cobra # only these keys
# Uses Android Studio's JDK and the Kotlin compiler bundled with the newest Gradle in ~/.gradle.
# Java prints harmless warnings on stderr; don't treat them as errors (PowerShell 5.1).
$java = 'C:\Program Files\Android\Android Studio\jbr\bin\java.exe'
$lib = if ($env:KOTLIN_LIB) { $env:KOTLIN_LIB } else {
    Get-ChildItem "$env:USERPROFILE\.gradle\wrapper\dists\*\*\*\lib" -Directory |
        Where-Object { Get-ChildItem $_.FullName -Filter 'kotlin-compiler-embeddable-*.jar' } |
        Sort-Object FullName | Select-Object -Last 1 -ExpandProperty FullName
}
$jars = 'kotlin-compiler-embeddable-*', 'kotlin-stdlib-2*', 'kotlin-script-runtime-*', 'kotlin-reflect-*',
        'kotlin-daemon-embeddable-*', 'trove4j*', 'annotations-*', 'kotlinx-coroutines-core-jvm-*' |
    ForEach-Object { Get-ChildItem $lib -Filter "$_.jar" } | ForEach-Object { $_.FullName }
$cp = $jars -join ';'
$std = (Get-ChildItem $lib -Filter 'kotlin-stdlib-2*.jar' | Select-Object -First 1).FullName
$src = 'app\src\main\java\dev\clean\stretch'
$out = 'tools\pose-preview\out'
$sources = @(Get-ChildItem "$src\figure\*.kt", "$src\catalog\*.kt" | ForEach-Object { $_.FullName }) + 'tools\pose-preview\PosePreview.kt'
& $java -cp $cp org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -no-stdlib -cp $std -nowarn @sources -d "$out\classes"
if ($LASTEXITCODE -ne 0) { throw 'Kotlin compile failed' }
& $java '-Djava.awt.headless=true' -cp "$out\classes;$std" PosePreviewKt $out @args
