package org.ibci.componentinstaller.model.util

import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel
import java.io.File

/**
 * Helper for python functionality
 *
 */
class PythonHelper {
    //<editor-fold desc="Class attributes">
    /**
     * The logger
     *
     */
    val fileLogger = FileLogger()
    //</editor-fold>

    /**
     * Setup venv
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun setupVenv(): Boolean {
        try {
            val process = ProcessBuilder("cmd.exe", "/C", "${PathDefinitions.PYSSA_PROGRAM_BIN_DIR}\\setup_python_for_pyssa\\setup_python.bat")
                .directory(File("${PathDefinitions.PYSSA_PROGRAM_BIN_DIR}\\setup_python_for_pyssa"))
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

            process.waitFor()
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
        return true
    }

    /**
     * Installs a Python wheel using pip
     *
     * @param aWheelFilepath The filepath of the wheel file to install
     * @throws IllegalArgumentException Thrown if [aWheelFilepath] does not exist
     * @throws NullPointerException Thrown if [aWheelFilepath] is null
     * @return True if operation is successful, false: Otherwise
     */
    fun pipWheelInstall(aWheelFilepath: String): Boolean {
        //<editor-fold desc="Checks">
        // Check if aWheelFilepath is null
        requireNotNull(aWheelFilepath) { "aWheelFilepath must not be null." }

        // Check if aWheelFilepath exists
        require(File(aWheelFilepath).exists()) { "aWheelFilepath does not exist." }
        //</editor-fold>

        try {
            val process = ProcessBuilder(PathDefinitions.PIP_EXE, "install", aWheelFilepath)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start()

            process.waitFor()
            return process.exitValue() == 0 // Return true if exit value is 0 (success)
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }
}
