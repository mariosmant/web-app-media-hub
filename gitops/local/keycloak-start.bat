@echo off
setlocal enableextensions

REM === Usage: start-keycloak.bat "C:\Path\To\JDK" "C:\Path\To\Keycloak" ===

REM --- Validate parameters ---
if "%~1"=="" goto :usage
if "%~2"=="" goto :usage

REM --- Set JAVA_HOME from parameter 1 ---
set "JAVA_HOME=%~1"
if not exist "%JAVA_HOME%\bin\java.exe" (
  echo ERROR: JAVA_HOME is invalid or java.exe not found: "%JAVA_HOME%\bin\java.exe"
  exit /b 1
)

REM --- Set KEYCLOAK_HOME from parameter 2 ---
set "KEYCLOAK_HOME=%~2"
if not exist "%KEYCLOAK_HOME%\bin\kc.bat" (
  echo ERROR: Keycloak launcher not found: "%KEYCLOAK_HOME%\bin\kc.bat"
  exit /b 1
)

REM --- Put the provided JDK on PATH (in front) ---
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM --- Start Keycloak ---
REM set KC_PROXY=reencrypt
set KC_HTTP_RELATIVE_PATH=/iam-service
set KC_HOSTNAME=localhost
set KC_FRONTEND_URL=https://localhost/iam-service/
REM set "KC_HTTPS_CERTIFICATE_FILE=%KEYCLOAK_HOME%\\conf\\keycloak-cert.pem"
REM set "KC_HTTPS_CERTIFICATE_KEY_FILE=%KEYCLOAK_HOME%\\conf\\keycloak-key.pem"
set KC_PROXY_HEADERS=forwarded
call "%KEYCLOAK_HOME%\bin\kc.bat" start

exit /b %errorlevel%

:usage
echo.
echo Usage:
echo   %~nx0 "C:\Path\To\JDK" "C:\Path\To\Keycloak"
echo.
echo Example:
echo   %~nx0 "C:\Java\jdk-21" "C:\servers\keycloak"
exit /b 2
