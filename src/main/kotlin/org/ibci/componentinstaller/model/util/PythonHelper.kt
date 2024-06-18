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
     */
    private val fileLogger = FileLogger()
    //</editor-fold>

    /**
     * Setup venv
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun setupVenv(): Boolean {
        try {
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            tmpCustomProcessBuilder.runCommand(arrayOf("/C", "${PathDefinitions.PYSSA_PROGRAM_BIN_DIR}\\setup_python_for_pyssa\\setup_python.bat"))
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
     * @return True if operation is successful, false: Otherwise
     */
    fun pipWheelInstall(aWheelFilepath: String): Boolean {
        //<editor-fold desc="Checks">
        require(File(aWheelFilepath).exists()) { "aWheelFilepath does not exist." }
        //</editor-fold>

        try {
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            tmpCustomProcessBuilder.runCommand(
                anExecutable = PathDefinitions.PIP_EXE,
                aCommand = arrayOf("install", aWheelFilepath)
            )
            return true
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }
}
