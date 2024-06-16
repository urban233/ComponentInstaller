package org.ibci.componentinstaller.model.util

import java.io.File
import java.nio.file.Paths

/**
 * Provides functionality for creating and removing system entry shortcuts such as desktop and start menu shortcuts
 *
 */
class SystemEntryHandler {
    /**
     * Creates a desktop shortcut
     *
     * @param aTargetPath Filepath to the start batch file
     * @param aShortcutName Name of the shortcut
     * @param aIconPath Filepath of the icon
     * @throws IllegalArgumentException if any of the arguments are null
     */
    fun createDesktopShortcut(aTargetPath: String?, aShortcutName: String?, aIconPath: String?) {
        //<editor-fold desc="Checks">
        require(aTargetPath.isNullOrEmpty()) { "targetPath is null or empty." }
        require(aShortcutName.isNullOrEmpty()) { "shortcutName is null or empty." }
        require(aIconPath.isNullOrEmpty()) { "iconPath is null or empty." }
        //</editor-fold>

        // Get the path to the desktop folder
        val desktopPath = System.getProperty("user.home") + "\\Desktop"

        // Combine the desktop path with the shortcut name
        val shortcutPath = Paths.get(desktopPath, "$aShortcutName.lnk").toString()

        // Create a WshShell instance
        val shell = Shell32.INSTANCE.ShellCreateLink()

        // Create a shortcut object
        val shortcut = PointerByReference()
        val result = shell.CreateLink(shortcut, WinNT.HRESULT(CLSCTX_INPROC_SERVER))

        if (COMUtils.FAILED(result)) {
            throw RuntimeException("Failed to create shortcut. HRESULT: ${Kernel32Util.formatMessage(result)}")
        }

        // Set the properties of the shortcut
        val iShellLink = Shell32.IShellLinkW(shortcut.value)
        iShellLink.setPath(aTargetPath)
        // If aTargetPath is null, it throws a KotlinNullPointerException
        iShellLink.setWorkingDirectory(File(aTargetPath!!).parent)

        // Set the icon for the shortcut
        if (!aIconPath.isNullOrEmpty() && File(aIconPath).exists()) {
            iShellLink.setIconLocation(aIconPath, 0)
        }

        // Save the shortcut
        val persistFile = iShellLink.queryInterface(Shell32.IPersistFile::class.java)
        persistFile.save(shortcutPath, true)
    }



}
