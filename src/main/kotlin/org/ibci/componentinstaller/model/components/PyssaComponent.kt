package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.mutableStateOf
import org.ibci.componentinstaller.model.util.Io
import org.ibci.componentinstaller.model.util.PythonHelper
import org.ibci.componentinstaller.model.util.SystemEntryHandler
import org.ibci.componentinstaller.model.util.definitions.ComponentDefinitions
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.model.util.definitions.UrlDefinitions
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel

import java.io.File
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
    override val localVersion: KotlinVersion
        get() = KotlinVersion(-1, 0, 0)

    /**
     * The remote component version
     * IMPORTANT: If remote version is unavailable, the major is -1!!!
     *
     */
    override val remoteVersion: KotlinVersion
        get() = KotlinVersion(-1, 0, 0)

    /**
     * Information about the component
     */
    override val componentInfo: ComponentInfo
        get() = ComponentInfo(
            aComponentLogoResourceFilepath = "component_logos/pyssa_96_dpi.png",
            aComponentDescription = "An easy-to-use protein structure research tool"
        )

    /**
     * The installation state
     */
    override var installedState = mutableStateOf(false)

    /**
     * The update state
     */
    override var updatableState = mutableStateOf(false)
    //</editor-fold>

    //<editor-fold desc="Install">
    /**
     * Install PySSA component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun install(): Boolean {
        // First installer prototype has only an online version, therefore no offline package is needed but an internet connection
        downloadWindowsPackage()
        // This copies also the downloaded windows package (this method does not differentiate between the two)
        copyWindowsPackage()
        installPyssa()
        return true
    }

    //<editor-fold desc="Windows package">
    /**
     * Downloads the Windows package for PySSA.
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun downloadWindowsPackage(): Boolean {
        // Create program dir under ProgramData
        if (!File(PathDefinitions.PYSSA_PROGRAM_DIR).exists()) {
            File(PathDefinitions.PYSSA_PROGRAM_DIR).mkdirs()
        }
        // Download windows pyssa package
        if (!Io.downloadFile(UrlDefinitions.PYSSA_WINDOWS_PACKAGE, "${PathDefinitions.PYSSA_PROGRAM_DIR}/windows_package.zip")) {
            fileLogger.append(LogLevel.ERROR, "The windows_package.zip could not be downloaded!")
            return false
        }
        return true
    }

    /**
     * Copy the offline windows package for PySSA.
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun copyWindowsPackage(): Boolean {
        try {
            // Create program dir under ProgramData
            val tmpProgramDir: File = File(PathDefinitions.PYSSA_PROGRAM_DIR)
            if (!tmpProgramDir.exists()) {
                tmpProgramDir.mkdirs()
            }
            // Copy the windows pyssa package
            val tmpTargetFile: File = File("${PathDefinitions.PYSSA_PROGRAM_DIR}/windows_package.zip")
            if (!tmpTargetFile.exists()) {
                val sourceFile: File = File(PathDefinitions.PYSSA_INSTALLER_OFFLINE_WIN_PACKAGE_ZIP)
                sourceFile.copyTo(tmpTargetFile, overwrite = true)
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, ex.toString())
            return false
        }
    }
    //</editor-fold>

    /**
     * Installs PySSA
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun installPyssa(): Boolean {
        if (!File("${PathDefinitions.PYSSA_PROGRAM_DIR}/windows_package.zip").exists()) {
            fileLogger.append(LogLevel.ERROR, "The windows_package.zip could not be found.")
            return false
        }
        if (!unzipWindowsPackage()) {
            return false
        }
        if (!createWindowsShortcuts()) {
            return false
        }
        if (!setupPythonEnvironment()) {
            return false
        }
        if (!postInstallCleanup()) {
            return false
        }
        return true
    }

    //<editor-fold desc="Helper methods for installPySSA()">
    /**
     * Unzips the windows_package.zip file to the specified directory
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun unzipWindowsPackage(): Boolean {
        val tmpZipFilePath: String = "${PathDefinitions.PYSSA_PROGRAM_DIR}/windows_package.zip"
        val tmpExtractPath: String = PathDefinitions.PYSSA_PROGRAM_DIR

        // Ensure the zip archive exists
        if (!File(tmpZipFilePath).exists()) {
            fileLogger.append(LogLevel.ERROR, "The windows_package.zip file is missing! Exit installation process now.")
            return false
        }

        // Unzip the archive
        try {
            ZipFile(tmpZipFilePath).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val tmpOutputFile: File = File(tmpExtractPath, entry.name)
                    if (entry.isDirectory) {
                        tmpOutputFile.mkdirs()
                    } else {
                        tmpOutputFile.outputStream().use { output ->
                            zip.getInputStream(entry).use { input ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "Extraction ended with error: ${ex.message}")
            return false
        }
        try {
            File(tmpZipFilePath).deleteRecursively()
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "Removing the offline windows package ended with error: ${ex.message}")
            return false
        }
        return true
    }

    /**
     * Creates Windows shortcuts for the PyMOL-PySSA application
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun createWindowsShortcuts(): Boolean {
        try {
            val tmpSystemEntryHandler: SystemEntryHandler = SystemEntryHandler()
            tmpSystemEntryHandler.createShortcuts()
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
    fun setupPythonEnvironment(): Boolean {
        try {
            val tmpPythonHelper: PythonHelper = PythonHelper()
            if (!tmpPythonHelper.setupVenv()) {
                fileLogger.append(LogLevel.ERROR, "Could not setup Python with .venv.")
                return false
            }
            if (!tmpPythonHelper.pipWheelInstall("${PathDefinitions.PYSSA_PROGRAM_DIR}/Pmw-2.1.1.tar.gz")) {
                fileLogger.append(LogLevel.ERROR, "Could not install Pmw.")
                return false
            }
            if (!tmpPythonHelper.pipWheelInstall("${PathDefinitions.PYSSA_PROGRAM_DIR}/pymol-3.0.0-cp311-cp311-win_amd64.whl")) {
                fileLogger.append(LogLevel.ERROR, "Could not install PyMOL.")
                return false
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

    /**
     * Cleans files when the installation is complete
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun postInstallCleanup(): Boolean {
        try {
            File("${PathDefinitions.PYSSA_PROGRAM_DIR}\\Pmw-2.1.1.tar.gz").deleteRecursively()
            File("${PathDefinitions.PYSSA_PROGRAM_DIR}\\pymol-3.0.0-cp311-cp311-win_amd64.whl").deleteRecursively()
            val tmpSetupPythonPath: File = Paths.get("${PathDefinitions.PYSSA_PROGRAM_BIN_DIR}\\setup_python_for_pyssa").toFile()
            if (tmpSetupPythonPath.exists()) {
                tmpSetupPythonPath.deleteRecursively()
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
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
    override fun uninstall(): Boolean {
        try {
            val tmpSystemEntryHandler: SystemEntryHandler = SystemEntryHandler()
            tmpSystemEntryHandler.removeShortcuts()
            File(PathDefinitions.PYSSA_PROGRAM_DIR).deleteRecursively()
            return true
        } catch (ex: AccessDeniedException) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        } catch (ex: Exception) {
            // Error occurred during one of the function calls, therefore return false
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }

//    /**
//     * Makes a cleanup after the installation of PySSA
//     *
//     * @return Empty string if cleanup is successful, otherwise: Exception message
//     */
//     fixme: Why should be the return type a string?
//    fun pyssaInstallationCleanup(): String {
//        SystemEntryHandler.removeShortcut(Environment.SpecialFolder.DesktopDirectory, "PyMOL-PySSA")
//        SystemEntryHandler.removeShortcut(Environment.SpecialFolder.StartMenu, "PyMOL-PySSA")
//        if (File(PathDefinitions.PYSSA_PROGRAM_DIR).exists()) {
//            try {
//                File(PathDefinitions.PYSSA_PROGRAM_DIR).deleteRecursively()
//            }
//            catch (ex: AccessDeniedException) {
//                fileLogger.append(LogLevel.WARNING, "$ex")
//                return "AccessDeniedExceptionxception: Unable to delete C:\\ProgramData\\pyssa directory."
//            }
//            catch (ex: Exception) {
//                // Error occurred during one of the function calls, return the exception message
//                fileLogger.append(LogLevel.ERROR, "$ex")
//                return ex.message ?: ""
//            }
//        }
//        return ""
//    }

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
    override fun update(): Boolean {
        try {
            // TODO: The method calls should be wrapped in if-statements
            uninstall()
            install()
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }

    /**
     * Updates only the sources of PySSA
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    fun updatePyssaSrc(): Boolean {
        try {
            File(PathDefinitions.PYSSA_RICH_CLIENT_DIR).deleteRecursively()
        }
        catch (ex: AccessDeniedException) {
            fileLogger.append(LogLevel.ERROR, "Error occurred during the delete process of the PySSA plugin path: $ex")
            return false
        }
        catch (ex: Exception) {
            // Error occurred during one of the function calls, return false
            return false
        }

        var tmpReturnCode = downloadWindowsPackage()
        if (!tmpReturnCode) {
            return false
        }
        tmpReturnCode = unzipWindowsPackage()
        if (!tmpReturnCode) {
            return false
        }
        tmpReturnCode = unzipPyssaPluginSrc()
        if (!tmpReturnCode) {
            return false
        }
        return true
    }

    /**
     * Checks if the component is installed
     *
     * @return True if component is installed, false: Otherwise
     */
    override fun isInstalled(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if the component has an update
     *
     * @return True if component has update, false: Otherwise
     */
    override fun hasUpdate(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if all prerequisite are met for an installation
     *
     * @return True if component can be installed, false: Otherwise
     */
    override fun checkPrerequisitesForInstallation(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if all prerequisite are met for an uninstallation
     *
     * @return True if component can be uninstalled, false: Otherwise
     */
    override fun checkPrerequisitesForUninstallation(): Boolean {
        TODO("Not yet implemented")
    }
    //</editor-fold>
}
