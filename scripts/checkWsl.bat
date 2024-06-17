@echo off

:: Check WSL version
for /f "tokens=*" %%i in ('wsl --list --verbose') do (
    echo %%i | findstr /C:"WSL 2" >nul
    if %errorlevel% equ 0 (
        echo WSL2 is installed.
        echo WSL2 is installed. > C:\ProgramData\IBCI\PySSA-Installer\.wsl_installed
        exit /b 0
    )
)

echo WSL2 is not installed.
exit /b 1
