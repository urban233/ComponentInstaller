package org.ibci.componentinstaller.model.util

import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel
import java.io.File
import java.nio.file.Paths

/**
 * Provides functionality for creating and removing system entry shortcuts such as desktop and start menu shortcuts
 *
 */
class SystemEntryHandler {
    //<editor-fold desc="Class attributes">
    /**
     * The logger
     *
     */
    val fileLogger = FileLogger()
    //</editor-fold>

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

        // Get the path to the desktop folder with platform-independent separator
        val desktopPath = System.getProperty("user.home") + File.separator + "Desktop"

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

        // Set working directory using safe get
        aTargetPath?.let {
            iShellLink.setWorkingDirectory(File(it).parent)
        }

        // Set the icon for the shortcut with existence check
        if (!aIconPath.isNullOrEmpty() && File(aIconPath).exists()) {
            iShellLink.setIconLocation(aIconPath, 0)
        }

        // Save the shortcut
        val persistFile = iShellLink.queryInterface(Shell32.IPersistFile::class.java)
        persistFile.save(shortcutPath, true)
    }

    /**
     * Creates a start menu shortcut
     *
     * @param aTargetPath Filepath to the start batch file
     * @param aShortcutName Name of the shortcut
     * @param aIconPath Filepath of the icon
     * @throws IllegalArgumentException if any of the arguments are null
     */
    fun createStartMenuShortcut(aTargetPath: String, aShortcutName: String, aIconPath: String) {
        //<editor-fold desc="Checks">
        requireNotNull(aTargetPath) { "targetPath is null." }
        requireNotNull(aShortcutName) { "shortcutName is null." }
        requireNotNull(aIconPath) { "iconPath is null." }
        //</editor-fold>

        // Get the path to the Start Menu Programs folder
        val startMenuPath = File(System.getenv("APPDATA"), "Microsoft\\Windows\\Start Menu\\Programs")
        val shortcutPath = File(startMenuPath, "$aShortcutName.lnk").path

        // Create a WshShellClass instance
        val shell = Class.forName("WScript.Shell").getConstructor().newInstance()

        // Create a shortcut object
        val shortcut = shell.javaClass.getMethod("CreateShortcut", String::class.java).invoke(shell, shortcutPath)

        // Set the properties of the shortcut
        shortcut.javaClass.getMethod("TargetPath").invoke(shortcut, aTargetPath)
        shortcut.javaClass.getMethod("WorkingDirectory").invoke(shortcut, File(aTargetPath).parent)

        // Set the icon for the shortcut
        if (File(aIconPath).exists()) {
            shortcut.javaClass.getMethod("IconLocation").invoke(shortcut, aIconPath)
        }

        // Save the shortcut
        shortcut.javaClass.getMethod("Save").invoke(shortcut)
    }

    /**
     * Removes the desktop and start menu shortcut
     *
     * @param aSpecialFolder Special folder like Desktop or StartMenu
     * @param aShortcutName Name of the shortcut
     * @throws IllegalArgumentException Thrown if any of the arguments are null
     */
    fun removeShortcut(aSpecialFolder: File, aShortcutName: String) {
        //<editor-fold desc="Checks">
        requireNotNull(aShortcutName) { "shortcutName is null." }
        //</editor-fold>

        // Get the folder path based on the specified special folder
        val folderPath = aSpecialFolder.path

        // Combine the folder path with the shortcut name
        val shortcutPath = Paths.get(folderPath, "$aShortcutName.lnk").toString()

        // Check if the shortcut file exists before attempting to delete it
        if (File(shortcutPath).exists()) {
            // Delete the shortcut file
            File(shortcutPath).delete()
            fileLogger.append(LogLevel.INFO, "Shortcut removed: $shortcutPath")
        }
        else {
            fileLogger.append(LogLevel.CRITICAL, "Shortcut not found: $shortcutPath")
        }
    }
}
