package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.mutableStateOf
import org.ibci.componentinstaller.model.util.Io
import org.ibci.componentinstaller.model.util.definitions.ComponentDefinitions
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.model.util.definitions.UrlDefinitions
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel

import java.io.File
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

    /**
     * Install a component
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
     * @return True if operation is successful, otherwise: false
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
     * @return True if operation is successful, otherwise: false
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
                val sourceFile = File(PathDefinitions.INSTALLER_OFFLINE_WIN_PACKAGE_ZIP_FILEPATH)
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
     * @return True if operation is successful, otherwise: false
     */
    // If it needs to run asynchronously use suspend after fun!
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
     * @return True if operation is successful, otherwise: false
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
     * @return True if operation is successful, otherwise: false
     */
    fun createWindowsShortcuts(): Boolean {
        try {
            // Specify the details for the shortcut to be created
            val executablePath = PathDefinitions.PYSSA_WINDOW_ARRANGEMENT_EXE_FILEPATH
            val shortcutName = "PySSA"
            val iconPath = PathDefinitions.PYSSA_ICON_FILEPATH

            TODO("Runs when SystemEntryHandler is full implemented!")
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
     * @return True if operation is successful, otherwise: false
     */
    fun setupPythonEnvironment(): Boolean {
        try {
            // TODO("Create PythonUtil  ")
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
     * Cleans files when the installation is complete
     *
     * @return True if operation is successful, otherwise: false
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

    /**
     * Uninstall a component
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    override fun uninstall(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Update a component
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    override fun update(): Boolean {
        TODO("Not yet implemented")
    }
}
