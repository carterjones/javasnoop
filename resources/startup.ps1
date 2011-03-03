Clear-Host

$unsafe_policy = "resources\unsafe.policy"
$safe_policy = "resources\safe.policy"
$user_policy = "$Env:USERPROFILE\.java.policy"

$java_home = $Env:JAVA_HOME

if ( $java_home.Contains("1.6") -or $java_home.Contains("1.7") ) {

   Write-Host "[1] Found 1.6+ Java in JAVA_HOME variable!"
   Copy-Item -Path "$java_home\lib\tools.jar" -Destination .\lib\tools.jar -Force   
   $jdk_exec = "$java_home\bin\javaw.exe"

} else {

   Write-Host "[1] JAVA_HOME variable wasn't set to a valid Java 1.6+ installation."
   Write-Host "Reading from registry to find other 1.6+ installations..."
   $java_home = (Get-ItemProperty "HKLM:\SOFTWARE\JavaSoft\Java Development Kit\1.6" JavaHome).JavaHome

   if ( ! $? ) {
      Write-Host "[2] Can't find Java installation through JAVA_HOME environment"
      Write-Host "    variable or through registry. Exiting."
      exit
   }

   Copy-Item -Path "$java_home\lib\tools.jar" -Destination .\lib\tools.jar -Force
   $jdk_exec = "$java_home\bin\javaw.exe"
}

Write-Host "[2] Turning off Java security for JavaSnoop usage."

Copy-Item -Path "$unsafe_policy" -Destination "$user_policy" -Force

Write-Host "[3] Starting JavaSnoop"
Start-Process "$jdk_exec" "-jar JavaSnoop.jar" -Wait -PassThru -WindowStyle minimized| Out-Null

Write-Host "[4] Turning Java security back on for safe browsing."
Remove-Item $user_policy
Copy-Item -Path $safe_policy -Destination $user_policy -Force