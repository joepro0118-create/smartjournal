$jhUser = [Environment]::GetEnvironmentVariable('JAVA_HOME','User')
$jhMachine = [Environment]::GetEnvironmentVariable('JAVA_HOME','Machine')
Write-Output ("User JAVA_HOME=[{0}]" -f $jhUser)
Write-Output ("Machine JAVA_HOME=[{0}]" -f $jhMachine)

