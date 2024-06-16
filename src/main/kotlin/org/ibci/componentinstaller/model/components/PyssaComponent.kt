package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.mutableStateOf
import org.ibci.componentinstaller.model.util.Io
import org.ibci.componentinstaller.model.util.SystemEntryHandler
import org.ibci.componentinstaller.model.util.definitions.ComponentDefinitions
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.model.util.definitions.UrlDefinitions
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel

import java.io.File
import java.nio.file.AccessDeniedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile

/**
 * Class for PySSA component
 *
 */
class PyssaComponent: IComponent {
    //<editor-fold desc="Class attributes">
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
     * The installation state
     */
    override var _installed = mutableStateOf(false)

    /**
     * Returns the installation state
     *
     * @return True if the component is installed, false: Otherwise
     */
    override fun isInstalled() = _installed.value

    /**
     * Sets the new value for the installation state
     *
     * @param value value A boolean that indicated the state of the installation
     */
    override fun setInstalled(value: Boolean) {
        _installed.value = value
    }

    /**
     * The update state
     */
    override var _updatable = mutableStateOf(false)

    /**
     * Returns the update state
     *
     * @return True if component has update, false: Otherwise
     */
    override fun hasUpdate() = _updatable.value

    /**
     * Sets the new value for the update state of the component
     *
     * @param value A boolean that indicated the state of the installation
     */
    override fun setUpdatable(value: Boolean) {
        _updatable.value = value
    }

    /**
     * The logger
     *
     */
    val fileLogger = FileLogger()
    //</editor-fold>

    //<editor-fold desc="Install">
    /**
     * Install PySSA component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun install(): Boolean {
        // TODO("Check if internet connection is available or not and select methods!")
        downloadWindowsPackage()
        copyOfflineWindowsPackage()
        installPyssa()
        return true
    }

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
        if (!Io.downloadFile(UrlDefinitions.PYSSA_WINDOWS_PACKAGE, "$PathDefinitions.PYSSA_PROGRAM_DIR/windows_package.zip")) {
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
    fun copyOfflineWindowsPackage(): Boolean {
        try {
            // Create program dir under ProgramData
            val programDir = File(PathDefinitions.PYSSA_PROGRAM_DIR)
            if (!programDir.exists()) {
                programDir.mkdirs()
            }

            // Copy the windows pyssa package
            val targetFile = File("${PathDefinitions.PYSSA_PROGRAM_DIR}/windows_package.zip")
            if (!targetFile.exists()) {
                val sourceFile = File(PathDefinitions.PYSSA_INSTALLER_OFFLINE_WIN_PACKAGE_ZIP)
                sourceFile.copyTo(targetFile, overwrite = true)
            }
            return true
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }

    /**
     * Installs PySSA
     *
     * @return True if operation is successful, false: Otherwise
     */
    // fixme: If it needs to run asynchronously use suspend after fun!
    fun installPyssa(): Boolean {
        if (!File("${PathDefinitions.PYSSA_PROGRAM_DIR}/windows_package.zip").exists()) {
            fileLogger.append(LogLevel.ERROR, "The windows_package.zip could not be found.")
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
        val zipFilePath = "${PathDefinitions.PYSSA_PROGRAM_DIR}/windows_package.zip"
        val extractPath = PathDefinitions.PYSSA_PROGRAM_DIR

        // Ensure the zip archive exists
        if (!File(zipFilePath).exists()) {
            fileLogger.append(LogLevel.ERROR, "The windows_package.zip file is missing! Exit installation process now.")
            return false
        }

        // Ensure the extract directory exists
        if (!File(extractPath).exists()) {
            File(extractPath).mkdirs()
        }

        // Unzip the archive
        try {
            ZipFile(zipFilePath).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val outputFile = File(extractPath, entry.name)
                    if (entry.isDirectory) {
                        outputFile.mkdirs()
                    }
                    else {
                        outputFile.outputStream().use { output ->
                            zip.getInputStream(entry).use { input ->
                                input.copyTo(output)
                            }
                        }
                    }
                }
            }
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "Extraction ended with error: ${ex.message}")
            return false
        }
        try {
            Files.delete(Paths.get(zipFilePath))
        }
        catch (ex: Exception) {
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
            // Specify the details for the shortcut to be created
            val executablePath = PathDefinitions.PYSSA_WINDOW_ARRANGEMENT_EXE
            val shortcutName = "PySSA"
            val iconPath = PathDefinitions.PYSSA_ICON

            // fixme: Runs when SystemEntryHandler is full implemented!
            // Create desktop icon
            SystemEntryHandler.createDesktopShortcut(executablePath, shortcutName, iconPath)
            // Create start menu entry
            SystemEntryHandler.createStartMenuShortcut(executablePath, shortcutName, iconPath)
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
        return true
    }

    /**
     * Configure the setup for python environment
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun setupPythonEnvironment(): Boolean {
        try {
            // fixme: Create PythonUtil
            val tmpPythonUtil = PythonUtil()
            if (!tmpPythonUtil.setupVenv()) {
                fileLogger.append(LogLevel.ERROR, "Could not setup Python with .venv.")
                return false
            }
            if (!tmpPythonUtil.pipWheelInstall("${PathDefinitions.PYSSA_PROGRAM_DIR}/Pmw-2.1.1.tar.gz")) {
                fileLogger.append(LogLevel.ERROR, "Could not install Pmw.")
                return false
            }
            if (!tmpPythonUtil.pipWheelInstall("${PathDefinitions.PYSSA_PROGRAM_DIR}/pymol-3.0.0-cp311-cp311-win_amd64.whl")) {
                fileLogger.append(LogLevel.ERROR, "Could not install PyMOL.")
                return false
            }
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
        return true
    }

    /**
     * Unzip the pyssa.zip file to the specified extract directory
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun unzipPyssaPluginSrc(): Boolean {
        // Unzip plugin to plugin dir location
        val zipFilePath = "${PathDefinitions.PYSSA_PROGRAM_DIR}\\pyssa.zip"
        val extractPath = "${PathDefinitions.PymolExeFilepath}\\pyssa"

        // Ensure the zip archive exists
        if (!File(zipFilePath).exists()) {
            fileLogger.append(LogLevel.ERROR, "The pyssa.zip file is missing! Exit installation process now.")
            return false
        }

        // Ensure the extract directory exists
        if (!File(extractPath).exists()) {
            File(extractPath).mkdirs()
        }

        // Unzip the archive
        try {
            ZipFile(zipFilePath).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val entryDestination = File(extractPath, entry.name)
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
        }
        catch (ex: Exception) {
            // Error while extracting pyssa.zip file, return false
            fileLogger.append(LogLevel.ERROR, "Extraction ended with error: $ex")
            return false
        }
        return true
    }

    /**
     * Cleans files when the installation is complete
     *
     * @return True if operation is successful, false: Otherwise
     */
    fun postInstallCleanup(): Boolean {
        try {
            Files.deleteIfExists(Paths.get("C:\\ProgramData\\IBCI\\PySSA\\Pmw-2.1.1.tar.gz"))
            Files.deleteIfExists(Paths.get("C:\\ProgramData\\IBCI\\PySSA\\pymol-3.0.0-cp311-cp311-win_amd64.whl"))
            val setupPythonPath = Paths.get("C:\\ProgramData\\IBCI\\PySSA\\bin\\setup_python_for_pyssa")
            if (Files.exists(setupPythonPath)) {
                Files.walk(setupPythonPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete)
            }
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
        return true
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="Uninstall">
    /**
     * Uninstall PySSA component
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    // fixme: If it needs to run asynchronously use suspend after fun!
    override fun uninstall(): Boolean {
        try {
            val shortcutName = "PySSA"
            SystemEntryHandler.removeShortcut(Environment.SpecialFolder.DesktopDirectory, shortcutName)
            SystemEntryHandler.removeShortcut(Environment.SpecialFolder.StartMenu, shortcutName)
            File("${PathDefinitions.PYSSA_PROGRAM_DIR}/win_start").deleteRecursively()
            File(PathDefinitions.PYSSA_PROGRAM_BIN_DIR).deleteRecursively()
            return true
        }
        catch (ex: AccessDeniedException) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
        catch (ex: Exception) {
            // Error occurred during one of the function calls, therefore return false
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
    }

    /**
     * Makes a cleanup after the installation of PySSA
     *
     * @return Empty string if cleanup is successful, otherwise: Exception message
     */
    // fixme: Why should be the return type a string?
    fun pyssaInstallationCleanup(): String {
        SystemEntryHandler.removeShortcut(Environment.SpecialFolder.DesktopDirectory, "PyMOL-PySSA")
        SystemEntryHandler.removeShortcut(Environment.SpecialFolder.StartMenu, "PyMOL-PySSA")
        if (File(PathDefinitions.PYSSA_PROGRAM_DIR).exists()) {
            try {
                File(PathDefinitions.PYSSA_PROGRAM_DIR).deleteRecursively()
            }
            catch (ex: AccessDeniedException) {
                fileLogger.append(LogLevel.WARNING, "$ex")
                return "AccessDeniedExceptionxception: Unable to delete C:\\ProgramData\\pyssa directory."
            }
            catch (ex: Exception) {
                // Error occurred during one of the function calls, return the exception message
                fileLogger.append(LogLevel.ERROR, "$ex")
                return ex.message ?: ""
            }
        }
        return ""
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
        }
        catch (ex: Exception) {
            println("ERROR: Removal process ended with error: $ex")
            return false
        }
        return true
    }
    //</editor-fold>

    //<editor-fold desc="Update">
    /**
     * Update PySSA component
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    // fixme: If it needs to run asynchronously use suspend after fun!
    override fun update(): Boolean {
        try {
            uninstall()
            install()
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            return false
        }
        return true
    }

    /**
     * Updates only the sources of PySSA
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    // fixme: If it needs to run asynchronously use suspend after fun!
    fun updatePyssaSrc(): Boolean {
        try {
            File(PathDefinitions.PYSSA_PLUGIN).deleteRecursively()
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
    //</editor-fold>
}
