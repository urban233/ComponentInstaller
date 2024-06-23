@echo off
setlocal

rem Check VirtualMachinePlatform feature status
dism.exe /Online /Get-FeatureInfo /FeatureName:VirtualMachinePlatform | findstr /i "State : Enabled" >nul
set vm_enabled=%errorlevel%

rem Check WSL version
set wsl_installed=0
for /f "tokens=*" %%i in ('wsl --list --verbose') do (
    echo %%i | findstr /C:"WSL 2" >nul
    if %errorlevel% equ 0 (
        set wsl_installed=1
    )
)

rem Determine final status
if %wsl_installed% equ 1 if %vm_enabled% equ 0 (
    echo WSL2 and VirtualMachinePlatform are installed.
    echo WSL2 and VirtualMachinePlatform are installed. > C:\ProgramData\IBCI\PySSA-Installer\.wsl_installed
    exit /b 0
) else (
    echo WSL2 and/or VirtualMachinePlatform are not installed.
    exit /b 1
)

endlocal
pause
