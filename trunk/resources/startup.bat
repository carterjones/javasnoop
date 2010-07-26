@echo off

setlocal ENABLEDELAYEDEXPANSION

if "%OS%" == "Windows_NT" setlocal
rem ---------------------------------------------------------------------------
rem Startup Script for JavaSnoop

rem   JAVA_HOME       (Optional) Must point at your Java Development Kit installation.
rem   JAVA_OPTS       (Optional) Java runtime options.

set unsafe_policy=resources\unsafe.policy
set safe_policy=resources\safe.policy
set user_policy=%USERPROFILE%\.java.policy

echo %JAVA_HOME% | find /i "1.6" > NUL

if not errorlevel 1 (
   echo [1] Found 1.6 Java in JAVA_HOME variable!
   copy "%JAVA_HOME%\lib\tools.jar" .\lib\tools.jar >NUL
   set JDK_EXEC=%JAVA_HOME%\bin\java.exe
) else (
   echo [1] JAVA_HOME variable wasn't set to a valid Java 1.6+ installation.
   echo     Reading from registry to find other 1.6 installations...
   reg query "HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Development Kit\1.6" /v JavaHome > jver.tmp

   if errorlevel 1 (
      del jver.tmp 2>NUL
      echo [2] Can't find Java installation through JAVA_HOME environment or
      echo     through registry. Exiting.
      goto :EOF
   )

   type jver.tmp |more /E +2 > jdk.tmp
   set /P jdk=<jdk.tmp
   set jdk=!jdk:~26!
   copy /Y "%jdk%\lib\tools.jar" .\lib\tools.jar >NUL
   set JDK_EXEC=!jdk!\bin\java.exe
   del jver.tmp 2>NUL
   del jdk.tmp 2>NUL
)

echo [2] Turning off Java security for JavaSnoop usage.
del %user_policy% 2>NUL
copy %unsafe_policy% %user_policy% >NUL

echo [3] Starting JavaSnoop
start /wait /MIN "JavaSnoop" "%JDK_EXEC%" -jar JavaSnoop.jar

echo [4] Turning Java security back on for safe browsing.
del %user_policy% 2> NUL
copy %safe_policy% %user_policy% > NUL

if "%OS%" == "Windows_NT" endlocal