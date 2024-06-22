package org.ibci.componentinstaller.model.util

import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.util.OperationTypeDefinitions
import org.ibci.componentinstaller.util.RequestData
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel
import org.ibci.extension.communication.Communicator
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
            fileLogger.append(LogLevel.INFO, "Creating python environment ...")
            val tmpCommunicator: Communicator = Communicator()
            val tmpData = RequestData(
                OperationTypeDefinitions.RUN_CMD_COMMAND,
                arrayOf("C:\\ProgramData\\IBCI\\PySSA\\bin\\setup_python_for_pyssa\\setup_python.bat")
            )
            if (!tmpData.writeToJsonFile()) {
                fileLogger.append(LogLevel.ERROR, "Writing data to json file failed!")
                return false
            }
            fileLogger.append(LogLevel.INFO, "Sending request to: Run command in cmd ...")
            if (!tmpCommunicator.sendRequest(PathDefinitions.EXCHANGE_JSON, false)) {
                fileLogger.append(LogLevel.ERROR, "Running the cmd command in the Windows wrapper failed!")
                return false
            } else {
                fileLogger.append(LogLevel.DEBUG, tmpCommunicator.lastReply)
            }
            fileLogger.append(LogLevel.INFO, "Creating python environment finished.")
            return true
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
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
            fileLogger.append(LogLevel.INFO, "Installing a python package with pip ...")
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            tmpCustomProcessBuilder.runCommand(arrayOf("/C", PathDefinitions.PIP_EXE,"install", aWheelFilepath)
            )
            fileLogger.append(LogLevel.INFO, "Installation of package finished.")
            return true
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }
}
