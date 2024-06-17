# Set variables
$ShortcutName = "PySSA"
$DesktopPath = [System.IO.Path]::Combine([System.Environment]::GetFolderPath("Desktop"), "$ShortcutName.lnk")
$StartMenuPath = [System.IO.Path]::Combine([System.Environment]::GetFolderPath("StartMenu"), "Programs", "$ShortcutName.lnk")

# Function to remove a shortcut
function Remove-Shortcut {
    param (
        [string]$shortcutLocation
    )
    
    if (Test-Path $shortcutLocation) {
        Remove-Item $shortcutLocation -Force
        Write-Output "Shortcut removed: $shortcutLocation"
    } else {
        Write-Output "Shortcut not found: $shortcutLocation"
    }
}

# Remove desktop shortcut
Remove-Shortcut -shortcutLocation $DesktopPath

# Remove start menu shortcut
Remove-Shortcut -shortcutLocation $StartMenuPath

Write-Output "Shortcut removal complete."
