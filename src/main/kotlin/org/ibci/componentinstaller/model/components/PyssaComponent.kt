package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.ibci.componentinstaller.gui.ComponentState
import org.ibci.componentinstaller.model.util.*
import org.ibci.componentinstaller.model.util.definitions.ComponentDefinitions
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.model.util.definitions.UrlDefinitions
import org.ibci.componentinstaller.util.OperationTypeDefinitions
import org.ibci.componentinstaller.util.RequestData
import org.ibci.componentinstaller.util.Utils
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel
import org.ibci.extension.communication.Communicator

import java.io.File
import java.net.InetAddress
import java.nio.file.AccessDeniedException
import java.nio.file.Paths
import java.util.zip.ZipFile

/**
 * Class for PySSA component
 *
 */
class PyssaComponent: IComponent {
    //<editor-fold desc="Class attributes">
    /**
     * The logger
     *
     */
    private val fileLogger = FileLogger()
    /**
     * The communicator for the windows wrapper
     *
     */
    private val communicator = Communicator()
    /**
     * The component name
     *
     */
    override val name: String
        get() = ComponentDefinitions.COMPONENT_NAME_PYSSA

    /**
     * The local component version
     * IMPORTANT: If version could not be found, the major is -1!!!
     *
     */
    override var localVersion: KotlinVersion = KotlinVersion(1, 0, 0)

    /**
     * The remote component version
     * IMPORTANT: If remote version is unavailable, the major is -1!!!
     *
     */
    override var remoteVersion: KotlinVersion = KotlinVersion(1, 0, 0)

    /**
     * Information about the component
     */
    override val componentInfo: ComponentInfo
        get() = ComponentInfo(
            aComponentLogoResourceFilepath = "assets/component_logos/pyssa_96_dpi.png",
            aComponentDescription = "An easy-to-use protein structure research tool",
            anInstallationLocation = PathDefinitions.PYSSA_PROGRAM_DIR
        )

    override var states: ComponentState = ComponentState(
        isInstalled(),
        hasUpdate()
    )
    //</editor-fold>

    //<editor-fold desc="Install">
    /**
     * Install PySSA component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override suspend fun install(): Boolean {
        // First installer prototype has only an online version, therefore no offline package is needed but an internet connection!
        try {
            communicator.startWindowsWrapper(false)
            delay(3000)
            val tmpWindowsPackage: File = File(PathDefinitions.PYSSA_INSTALLER_WINDOWS_PACKAGE_ZIP)
            // First check if anything is stored offline
            if (tmpWindowsPackage.exists()) {
                if (!copyWindowsPackage()) {
                    stopCommunicator() // TODO: This is bad and needs to be fixed in the future!
                    return false
                }
            } else {
                // No offline files found & try to download
                if (Utils.isInternetAvailable()) {
                    if (!downloadWindowsPackage()) {
                        stopCommunicator()
                        return false
                    }
                } else {
                    stopCommunicator()
                    return false
                }
            }
            if (!unzipWindowsPackage()) {
                stopCommunicator()
                return false
            }
            if (!createWindowsShortcuts()) {
                stopCommunicator()
                return false
            }
            if (!setupPythonEnvironment()) {
                stopCommunicator()
                return false
            }
            if (!postInstallCleanup()) {
                stopCommunicator()
                return false
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            stopCommunicator()
            return false
        } finally {
            stopCommunicator()
        }
    }

    /**
     * TODO: This method needs to be implemented in the Communicator class in src/main/java!!
     * This is only done to make it work for now.
     */
    fun stopCommunicator() : Boolean {
        val tmpData = RequestData(
            OperationTypeDefinitions.CLOSE_CONNECTION,
            arrayOf("Close connection.")
        )
        if (!tmpData.writeToJsonFile()) {
            fileLogger.append(LogLevel.ERROR, "Writing data to json file failed!")
            return false
        }
        fileLogger.append(LogLevel.INFO, "Sending request to: Close connection ...")
        if (!communicator.sendRequest(PathDefinitions.EXCHANGE_JSON)) {
            fileLogger.append(LogLevel.ERROR, "Close connection failed!")
            return false
        } else {
            fileLogger.append(LogLevel.DEBUG, communicator.lastReply)
            return true
        }
    }

    //<editor-fold desc="Windows package">
    /**
     * Downloads the Windows package for PySSA.
     *
     * @return True if operation is successful, false: Otherwise
     */
    private fun downloadWindowsPackage(): Boolean {
        // Create program dir under ProgramData
        if (!File(PathDefinitions.PYSSA_PROGRAM_DIR).exists()) {
            File(PathDefinitions.PYSSA_PROGRAM_DIR).mkdirs()
        }
        fileLogger.append(LogLevel.INFO, "Downloading windows_package.zip ...")
        // Download windows pyssa package
        if (!Io.downloadFile(UrlDefinitions.PYSSA_WINDOWS_PACKAGE, PathDefinitions.PYSSA_INSTALLER_WINDOWS_PACKAGE_ZIP)) {
            fileLogger.append(LogLevel.ERROR, "The windows_package.zip could not be downloaded!")
            return false
        }
        fileLogger.append(LogLevel.INFO, "Downloading windows_package.zip finished.")
        return true
    }

    /**
     * Copy the Windows package for PySSA.
     *
     * @return True if operation is successful, false: Otherwise
     */
    private fun copyWindowsPackage(): Boolean {
        try {
            // Create program dir under ProgramData
            val tmpProgramDir: File = File(PathDefinitions.PYSSA_PROGRAM_DIR)
            if (!tmpProgramDir.exists()) {
                tmpProgramDir.mkdirs()
            }
            // Copy the windows pyssa package
            val tmpTargetFile: File = File("${PathDefinitions.PYSSA_PROGRAM_DIR}/windows_package.zip")
            if (!tmpTargetFile.exists()) {
                val sourceFile: File = File(PathDefinitions.PYSSA_INSTALLER_WINDOWS_PACKAGE_ZIP)
                sourceFile.copyTo(tmpTargetFile, overwrite = true)
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, ex.toString())
            return false
        }
    }
    //</editor-fold>

    //<editor-fold desc="Helper methods for install()">
    /**
     * Unzips the windows_package.zip file to the specified directory
     *
     * @return True if operation is successful, false: Otherwise
     */
    private suspend fun unzipWindowsPackage(): Boolean {
        val tmpZipFilePath: File = File(PathDefinitions.PYSSA_INSTALLER_WINDOWS_PACKAGE_ZIP)
        val tmpExtractPath: File = File(PathDefinitions.PYSSA_PROGRAM_DIR)

        try {
            val tmpData = RequestData(
                OperationTypeDefinitions.UNZIP_ARCHIVE,
                arrayOf(
                    tmpZipFilePath.absolutePath,
                    tmpExtractPath.absolutePath
                )
            )
            if (!tmpData.writeToJsonFile()) {
                fileLogger.append(LogLevel.ERROR, "Writing data to json file failed!")
                return false
            }
            fileLogger.append(LogLevel.INFO, "Sending request to: Unzip windows_package.zip ...")
            if (!communicator.sendRequest(PathDefinitions.EXCHANGE_JSON)) {
                fileLogger.append(LogLevel.ERROR, "Unzip of windows_package.zip with the Windows wrapper failed!")
                return false
            } else {
                fileLogger.append(LogLevel.DEBUG, communicator.lastReply)
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }

    /**
     * Creates Windows shortcuts for the PyMOL-PySSA application
     *
     * @return True if operation is successful, false: Otherwise
     */
    private suspend fun createWindowsShortcuts(): Boolean {
        try {
            val tmpData = RequestData(
                OperationTypeDefinitions.CREATE_SHORTCUTS,
                arrayOf(
                    PathDefinitions.PYSSA_WINDOW_ARRANGEMENT_EXE,
                    "PySSA",
                    PathDefinitions.PYSSA_LOGO_ICO
                )
            )
            if (!tmpData.writeToJsonFile()) {
                fileLogger.append(LogLevel.ERROR, "Writing data to json file failed!")
                return false
            }
            fileLogger.append(LogLevel.INFO, "Sending request to: Create Windows shortcuts ...")
            if (!communicator.sendRequest(PathDefinitions.EXCHANGE_JSON)) {
                fileLogger.append(LogLevel.ERROR, "Creating shortcuts with the Windows wrapper failed!")
                return false
            } else {
                fileLogger.append(LogLevel.DEBUG, communicator.lastReply)
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }

    /**
     * Configure the setup for python environment
     *
     * @return True if operation is successful, false: Otherwise
     */
    private fun setupPythonEnvironment(): Boolean {
        try {
            fileLogger.append(LogLevel.INFO, "Starting to setup the python environment ...")
            val tmpPythonHelper: PythonHelper = PythonHelper()
            if (!tmpPythonHelper.setupVenv()) {
                fileLogger.append(LogLevel.ERROR, "Could not setup Python with .venv.")
                return false
            }
            if (!tmpPythonHelper.pipWheelInstall("${PathDefinitions.PYSSA_PROGRAM_DIR}\\Pmw-2.1.1.tar.gz")) {
                fileLogger.append(LogLevel.ERROR, "Could not install Pmw.")
                return false
            }
            if (!tmpPythonHelper.pipWheelInstall("${PathDefinitions.PYSSA_PROGRAM_DIR}\\pymol-3.0.0-cp311-cp311-win_amd64.whl")) {
                fileLogger.append(LogLevel.ERROR, "Could not install PyMOL.")
                return false
            }
            fileLogger.append(LogLevel.INFO, "Python environment was successfully setup.")
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }

    /**
     * Cleans files when the installation is complete
     *
     * @return True if operation is successful, false: Otherwise
     */
    private fun postInstallCleanup(): Boolean {
        try {
            File("${PathDefinitions.PYSSA_PROGRAM_DIR}\\Pmw-2.1.1.tar.gz").deleteRecursively()
            File("${PathDefinitions.PYSSA_PROGRAM_DIR}\\pymol-3.0.0-cp311-cp311-win_amd64.whl").deleteRecursively()
            val tmpSetupPythonPath: File = File("${PathDefinitions.PYSSA_PROGRAM_BIN_DIR}\\setup_python_for_pyssa")
            if (tmpSetupPythonPath.exists()) {
                tmpSetupPythonPath.deleteRecursively()
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }

    /**
     * Unzip the pyssa.zip file to the specified extract directory
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun unzipPyssaPluginSrc(): Boolean {
        // Unzip plugin to plugin dir location
        val tmpZipFilePath: String = "${PathDefinitions.PYSSA_PROGRAM_DIR}\\pyssa.zip"
        val tmpExtractPath: String = "${PathDefinitions.PYSSA_PROGRAM_BIN_DIR}\\PySSA"

        // Ensure the zip archive exists
        if (!File(tmpZipFilePath).exists()) {
            fileLogger.append(LogLevel.ERROR, "The pyssa.zip file is missing! Exit installation process now.")
            return false
        }

        // Ensure the extract directory exists
        if (!File(tmpExtractPath).exists()) {
            File(tmpExtractPath).mkdirs()
        }

        // Unzip the archive
        try {
            ZipFile(tmpZipFilePath).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val entryDestination: File = File(tmpExtractPath, entry.name)
                    if (entry.isDirectory) {
                        entryDestination.mkdirs()
                    }
                    else {
                        entryDestination.parentFile.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            entryDestination.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
            fileLogger.append(LogLevel.INFO, "Extraction successful!")
            return true
        } catch (ex: Exception) {
            // Error while extracting pyssa.zip file, return false
            fileLogger.append(LogLevel.ERROR, "Extraction ended with error: ${ex.toString()}")
            return false
        }
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="Uninstall">
    /**
     * Uninstall PySSA component
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    override suspend fun uninstall(): Boolean {
        try {
            communicator.startWindowsWrapper(false)
            delay(3000)
            removeShortcuts()
            File(PathDefinitions.PYSSA_PROGRAM_DIR).deleteRecursively()
        } catch (ex: AccessDeniedException) {
            fileLogger.append(LogLevel.ERROR, "There is a filesystem access violation. See this: $ex")
            stopCommunicator()
            return false
        } catch (ex: Exception) {
            // Error occurred during one of the function calls, therefore return false
            fileLogger.append(LogLevel.ERROR, "$ex")
            stopCommunicator()
            return false
        }
        stopCommunicator()
        return true
    }

    private suspend fun removeShortcuts() : Boolean {
        try {
            val tmpData = RequestData(OperationTypeDefinitions.REMOVE_SHORTCUTS, arrayOf("PySSA"))
            if (!tmpData.writeToJsonFile()) {
                fileLogger.append(LogLevel.ERROR, "Writing data to json file failed!")
                return false
            }
            fileLogger.append(LogLevel.INFO, "Sending request to: Remove Windows shortcuts ...")
            if (!communicator.sendRequest(PathDefinitions.EXCHANGE_JSON)) {
                fileLogger.append(LogLevel.ERROR, "Creating shortcuts with the Windows wrapper failed!")
                return false
            } else {
                fileLogger.append(LogLevel.DEBUG, communicator.lastReply)
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }

    /**
     * Removes the .pyssa directory from the user directory
     *
     * @return @return True if operation is successful, false: Otherwise
     */
    fun cleanUserDotPyssaFolder(theAbsoluteFilepathToTheUserHomeDirectory: String): Boolean {
        if (!File("${theAbsoluteFilepathToTheUserHomeDirectory}\\.pyssa").exists()) {
            fileLogger.append(LogLevel.INFO, ".pyssa directory does not exist. Therefore nothing was removed.")
            return true
        }
        try {
            File("${theAbsoluteFilepathToTheUserHomeDirectory}\\.pyssa").deleteRecursively()
            return true
        }
        catch (ex: Exception) {
            println("ERROR: Removal process ended with error: $ex")
            return false
        }
    }
    //</editor-fold>

    //<editor-fold desc="Update">
    /**
     * Update PySSA component
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    override suspend fun update(aSystemState: State<List<Boolean>>): Boolean {
        try {
            if (checkPrerequisitesForUninstallation(aSystemState)) {
                uninstall()
            }
            if (checkPrerequisitesForInstallation(aSystemState)) {
                install()
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }

//    /**
//     * Updates only the sources of PySSA
//     *
//     * @return True if component is successfully updated, false: Otherwise
//     */
//    fun updatePyssaSrc(): Boolean {
//        // Might be outdated
//        try {
//            File(PathDefinitions.PYSSA_RICH_CLIENT_DIR).deleteRecursively()
//        }
//        catch (ex: AccessDeniedException) {
//            fileLogger.append(LogLevel.ERROR, "Error occurred during the delete process of the PySSA plugin path: $ex")
//            return false
//        }
//        catch (ex: Exception) {
//            // Error occurred during one of the function calls, return false
//            return false
//        }
//
//        var tmpReturnCode = downloadWindowsPackage()
//        if (!tmpReturnCode) {
//            return false
//        }
//        tmpReturnCode = unzipWindowsPackage()
//        if (!tmpReturnCode) {
//            return false
//        }
//        tmpReturnCode = unzipPyssaPluginSrc()
//        if (!tmpReturnCode) {
//            return false
//        }
//        return true
//    }

    /**
     * Checks if the component is installed
     *
     * @return True if component is installed, false: Otherwise
     */
    override fun isInstalled(): Boolean {
        return File(PathDefinitions.PYSSA_WINDOW_ARRANGEMENT_EXE).exists()
    }

    /**
     * Checks if the component has an update
     *
     * @return True if component has update, false: Otherwise
     */
    override fun hasUpdate(): Boolean {
        if (Utils.isInternetAvailable()) {
            if (isInstalled()) {
                val localVersionHistory: VersionHistory = VersionHelper.createVersionHistoryFromLocalFile(
                    PathDefinitions.PYSSA_VERSION_HISTORY_JSON
                )
                localVersion = localVersionHistory.getLatestVersion().getAsKotlinVersion()
                val remoteVersionHistory: VersionHistory = VersionHelper.createVersionHistoryFromRemoteSource(
                    UrlDefinitions.PYSSA_RICH_CLIENT_VERSION_HISTORY
                )
                remoteVersion = remoteVersionHistory.getLatestVersion().getAsKotlinVersion()
                return remoteVersionHistory.compareAgainstLatestVersionOfHistory(localVersionHistory.getLatestVersion()) == -1
            }
            return false
        } else {
            val localVersionHistory: VersionHistory = VersionHelper.createVersionHistoryFromLocalFile(
                PathDefinitions.PYSSA_VERSION_HISTORY_JSON
            )
            localVersion = localVersionHistory.getLatestVersion().getAsKotlinVersion()
        }
        return false
    }

    /**
     * Checks if all prerequisite are met for an installation
     *
     * @return True if component can be installed, false: Otherwise
     */
    override fun checkPrerequisitesForInstallation(aSystemState: State<List<Boolean>>): Boolean {
        // 0 -> WSL2, 1 -> ColabFold
        fileLogger.append(LogLevel.DEBUG, "These are the values for 0 -> ${aSystemState.value[0]} and 1 -> ${aSystemState.value[1]}")
        if (aSystemState.value[0] && aSystemState.value[1]) {
            val tmpWindowsPackage: File = File("${PathDefinitions.PYSSA_PROGRAM_DIR}/windows_package.zip")
            // First check if anything is stored offline
            if (tmpWindowsPackage.exists()) {
                return true
            }
            if (Utils.isInternetAvailable()) {
                return true
            }
            return false
        } else {
            return false
        }
    }

    /**
     * Checks if all prerequisite are met for an uninstallation
     *
     * @return True if component can be uninstalled, false: Otherwise
     */
    override fun checkPrerequisitesForUninstallation(aSystemState: State<List<Boolean>>): Boolean {
        if (aSystemState.value[0] && aSystemState.value[1]) {
            return true
        } else {
            return false
        }
    }
    //</editor-fold>
}
