; Script generated by the Inno Script Studio Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
WizardImageFile=compiler:WizClassicImage.bmp
AppName=PySSA-Installer
AppVersion=0.0.3
AppCopyright=Martin Urban, Hannah Kullik, IBCI
AppId={{192F52C3-D86D-4735-9929-C7DF599CB534}
DefaultDirName={commonappdata}\IBCI\PySSA-Installer
AppPublisher=IBCI
VersionInfoProductName=PySSA-Installer
VersionInfoProductVersion=0.1.0
MinVersion=0,6.2
OutputDir=out
OutputBaseFilename=pyssa_installer_offline_setup
VersionInfoCopyright=GNU GPL v3
DisableDirPage=True
DisableProgramGroupPage=True
ArchitecturesInstallIn64BitMode=x64
WizardStyle=modern
DisableReadyPage=True
UninstallDisplayName=PySSA Installer
UninstallDisplayIcon={app}\assets\icon.ico
; This is necessary because the setup will exceed 2 GB (due to almalinux rootfs)
DiskSpanning=yes

[Dirs]
Name: "{commonappdata}\IBCI"
Name: "{commonappdata}\IBCI\PySSA-Installer"
Name: "{commonappdata}\IBCI\PySSA-Installer\assets"
Name: "{commonappdata}\IBCI\PySSA-Installer\bin"
Name: "{commonappdata}\IBCI\PySSA-Installer\temp"
Name: "{commonappdata}\IBCI\PySSA-Installer\tools"

[Files]
Source: "..\..\..\..\build\compose\binaries\main\app\ComponentInstaller\*"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\bin"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "..\scripts\*"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\scripts"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "..\..\..\..\WindowsWrapper\WindowsCmdElevator\bin\Release\net8.0\publish\win-x64\WindowsCmdElevator.exe"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\tools"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "..\..\..\..\WindowsWrapper\WindowsTasks\bin\Release\net8.0\publish\win-x64\WindowsTasks.exe"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\tools"; Flags: ignoreversion recursesubdirs createallsubdirs;

Source: "..\assets\icon.ico"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\assets"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "..\version_history.json"; DestDir: "{commonappdata}\IBCI\PySSA-Installer"; Flags: ignoreversion recursesubdirs createallsubdirs;
; These files are only for the offline installation
Source: "..\..\..\..\offline_resources\windows_package.zip"; DestDir: "{commonappdata}\IBCI\PySSA-Installer"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "..\..\..\..\offline_resources\alma-colabfold-9-rootfs.tar"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\temp"; Flags: ignoreversion recursesubdirs createallsubdirs;

[Icons]
Name: "{commondesktop}\PySSA-Installer"; Filename: "{commonappdata}\IBCI\PySSA-Installer\bin\ComponentInstaller.exe"; IconFilename: "{commonappdata}\IBCI\PySSA-Installer\assets\icon.ico"
Name: "{commonstartmenu}\PySSA-Installer"; Filename: "{commonappdata}\IBCI\PySSA-Installer\bin\ComponentInstaller.exe"; IconFilename: "{commonappdata}\IBCI\PySSA-Installer\assets\icon.ico"