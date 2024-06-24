; Script generated by the Inno Script Studio Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
WizardImageFile=compiler:WizClassicImage.bmp
AppName=PySSA-Installer
AppVersion={#AppVersion}
AppCopyright=Martin Urban, Hannah Kullik, IBCI
AppId={{192F52C3-D86D-4735-9929-C7DF599CB534}
DefaultDirName={commonappdata}\IBCI\PySSA-Installer
AppPublisher=IBCI
VersionInfoProductName=PySSA-Component-Installer
VersionInfoProductVersion=0.1.0
MinVersion=0,6.2
OutputDir=out
OutputBaseFilename=pyssa_installer_setup
VersionInfoCopyright=GNU GPL v3
DisableDirPage=True
DisableProgramGroupPage=True
ArchitecturesInstallIn64BitMode=x64
WizardStyle=modern
DisableReadyPage=True
UninstallDisplayName=PySSA Component Installer
UninstallDisplayIcon={app}\assets\icon.ico

[Dirs]
Name: "{commonappdata}\IBCI"
Name: "{commonappdata}\IBCI\PySSA-Installer"
Name: "{commonappdata}\IBCI\PySSA-Installer\assets"
Name: "{commonappdata}\IBCI\PySSA-Installer\bin"
Name: "{commonappdata}\IBCI\PySSA-Installer\tools"

[Files]
Source: "src\bin\*"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\bin"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "src\scripts\*"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\scripts"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "src\tools\*"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\tools"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "src\assets\icon.ico"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\assets"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "src\version_history.json"; DestDir: "{commonappdata}\IBCI\PySSA-Installer"; Flags: ignoreversion recursesubdirs createallsubdirs;
Source: "src\PySSAComponentInstaller-UserGuide.pdf"; DestDir: "{commonappdata}\IBCI\PySSA-Installer"; Flags: ignoreversion recursesubdirs createallsubdirs;
;Source: "src\offline_resources\windows_package.zip"; DestDir: "{commonappdata}\IBCI\PySSA-Installer\temp"; Flags: ignoreversion recursesubdirs createallsubdirs;

[Icons]
Name: "{commondesktop}\PySSA-Installer"; Filename: "{commonappdata}\IBCI\PySSA-Installer\bin\ComponentInstaller.exe"; IconFilename: "{commonappdata}\IBCI\PySSA-Installer\assets\icon.ico"
Name: "{commonstartmenu}\PySSA-Installer"; Filename: "{commonappdata}\IBCI\PySSA-Installer\bin\ComponentInstaller.exe"; IconFilename: "{commonappdata}\IBCI\PySSA-Installer\assets\icon.ico"
