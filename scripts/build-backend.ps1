$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
Set-Location -LiteralPath $root

# Force JAVA_HOME to the detected JDK and persist only for this process
$javaHome = 'C:\Program Files\Java\jdk-25'
if (!(Test-Path "$javaHome\bin\java.exe")) {
  throw "java.exe not found under $javaHome"
}
$env:JAVA_HOME = $javaHome
$env:Path = "$javaHome\bin;" + $env:Path

Write-Host "JAVA_HOME=$env:JAVA_HOME"

# Use Maven wrapper to build
& .\mvnw.cmd -DskipTests package
if ($LASTEXITCODE -ne 0) { throw "Build failed with exit code $LASTEXITCODE" }

$jar = Join-Path $root 'target\FOP_ASSIGNMENT-1.0-SNAPSHOT.jar'
if (!(Test-Path $jar)) { throw "Jar not found: $jar" }
Write-Host "Built jar: $jar"

