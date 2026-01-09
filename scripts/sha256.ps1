param(
  [Parameter(Mandatory=$true)][string]$Text
)

$bytes = [Text.Encoding]::UTF8.GetBytes($Text)
$sha = [System.Security.Cryptography.SHA256]::Create()
$hash = $sha.ComputeHash($bytes)
$hex = ($hash | ForEach-Object { $_.ToString('x2') }) -join ''
Write-Output $hex
