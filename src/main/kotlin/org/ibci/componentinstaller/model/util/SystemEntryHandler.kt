package org.ibci.componentinstaller.model.util

import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
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
    private val fileLogger = FileLogger()
    //</editor-fold>

    /**
     * Creates a desktop and start menu shortcut
     *
     * @return True if the operation was successful, false: Otherwise
     */
    fun createShortcuts(): Boolean {
        try {
            if (!File(PathDefinitions.CREATE_SHORTCUTS_PS1).exists()) {
                fileLogger.append(LogLevel.ERROR, "Unable to find createShortcuts.ps1 file!")
                return false
            }
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            tmpCustomProcessBuilder.runCommand(
                arrayOf("/C", "powershell", "-ExecutionPolicy", "Bypass", "-File", PathDefinitions.CREATE_SHORTCUTS_PS1)
            )
            return true
        } catch (ex: Exception) {
            return false
        }
    }

    /**
     * Removes a desktop and start menu shortcut
     *
     * @return True if the operation was successful, false: Otherwise
     */
    fun removeShortcuts(): Boolean {
        try {
            if (!File(PathDefinitions.REMOVE_SHORTCUTS_PS1).exists()) {
                fileLogger.append(LogLevel.ERROR, "Unable to find removeShortcuts.ps1 file!")
                return false
            }
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            tmpCustomProcessBuilder.runCommand(
                arrayOf("/C", "powershell", "-ExecutionPolicy", "Bypass", "-File", PathDefinitions.REMOVE_SHORTCUTS_PS1)
            )
            return true
        } catch (ex: Exception) {
            return false
        }
    }
}
