@echo off
setlocal

set "projectDirPath=%cd%"
set "baseDestinationDir=%projectDirPath%\deployment\inno_setup\src"
rmdir /q /s %baseDestinationDir%

xcopy /s /y "%projectDirPath%\build\compose\binaries\main\app\ComponentInstaller\*" "%baseDestinationDir%\bin\"
xcopy /s /y "%projectDirPath%\src\main\resources\scripts\*" "%baseDestinationDir%\scripts\"

rem offline resources
xcopy /s /y "%projectDirPath%\deployment\offline_resources\*" "%baseDestinationDir%\offline_resources\"
xcopy /s /y "%projectDirPath%\deployment\extras\*" "%baseDestinationDir%\tools\"

xcopy /y "%projectDirPath%\WindowsWrapper\WindowsCmdElevator\bin\Release\net8.0\publish\win-x64\WindowsCmdElevator.exe" "%baseDestinationDir%\tools\"
xcopy /y "%projectDirPath%\WindowsWrapper\WindowsTasks\bin\Release\net8.0\publish\win-x64\WindowsTasks.exe" "%baseDestinationDir%\tools\"
xcopy /y "%projectDirPath%\src\main\resources\assets\icon.ico" "%baseDestinationDir%\assets\"
xcopy /y "%projectDirPath%\src\main\resources\version_history.json" "%baseDestinationDir%\"
xcopy /y "%projectDirPath%\deployment\inno_setup\PySSA-Component-Installer-User-Guide.pdf" "%baseDestinationDir%\"

endlocal
