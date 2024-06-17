# Set variables
$TargetPath = "C:\ProgramData\IBCI\PySSA\win_start\vb_script\window_arrangement.exe"
$IconPath = "C:\ProgramData\IBCI\PySSA\win_start\images\logo.ico"
$ShortcutName = "PySSA"
$DesktopPath = [System.IO.Path]::Combine([System.Environment]::GetFolderPath("Desktop"), "$ShortcutName.lnk")
$StartMenuPath = [System.IO.Path]::Combine([System.Environment]::GetFolderPath("StartMenu"), "Programs", "$ShortcutName.lnk")

# Function to create a shortcut
function Create-Shortcut {
    param (
        [string]$shortcutLocation,
        [string]$targetPath,
        [string]$iconPath
    )
    
    $WScriptShell = New-Object -ComObject WScript.Shell
    $Shortcut = $WScriptShell.CreateShortcut($shortcutLocation)
    $Shortcut.TargetPath = $targetPath
    $Shortcut.IconLocation = $iconPath
    $Shortcut.Save()
}

# Create desktop shortcut
Create-Shortcut -shortcutLocation $DesktopPath -targetPath $TargetPath -iconPath $IconPath

# Create start menu shortcut
Create-Shortcut -shortcutLocation $StartMenuPath -targetPath $TargetPath -iconPath $IconPath

Write-Output "Shortcuts created successfully."
