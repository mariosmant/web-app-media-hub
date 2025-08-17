@echo off
setlocal enableextensions

REM === Usage: start-nginx.bat "C:\Path\To\Nginx" ===

REM --- Validate parameter 1 (nginx folder) ---
if "%~1"=="" goto :usage

set "NGINX_HOME=%~1"

REM --- Confirm nginx.exe exists ---
if not exist "%NGINX_HOME%\nginx.exe" (
    echo ERROR: nginx.exe not found in "%NGINX_HOME%"
    exit /b 1
)

REM --- Run nginx with full path ---
"%NGINX_HOME%\nginx.exe" -g "daemon off;"

REM --- Optional pause for visibility if nginx exits ---
pause

exit /b %errorlevel%

:usage
echo.
echo Usage:
echo   %~nx0 "C:\Path\To\Nginx"
echo.
echo Example:
echo   %~nx0 "C:\nginx-1.27.0"
exit /b 2
