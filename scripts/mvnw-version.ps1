$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
Set-Location -LiteralPath $root

$javaHome = 'C:\Program Files\Java\jdk-25'
$env:JAVA_HOME = $javaHome
$env:Path = "$javaHome\bin;" + $env:Path

& .\mvnw.cmd -v
exit $LASTEXITCODE

