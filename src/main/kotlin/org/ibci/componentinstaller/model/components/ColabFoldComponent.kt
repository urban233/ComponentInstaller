package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.ibci.componentinstaller.gui.ComponentState
import org.ibci.componentinstaller.model.util.CustomProcessBuilder
import org.ibci.componentinstaller.model.util.Io.downloadFile
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

/**
 * Class for ColabFold component
 *
 */
class ColabFoldComponent: IComponent {
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
     * The ColabFold name
     *
     */
    override val name: String
        get() = ComponentDefinitions.COMPONENT_NAME_COLABFOLD

    /**
     * The local ColabFold component version
     * IMPORTANT: If version could not be found, the major is -1!!!
     *
     */
    override val localVersion: KotlinVersion
        get() = KotlinVersion(1, 5, 3)

    /**
     * The remote ColabFold component version
     * IMPORTANT: If remote version is unavailable, the major is -1!!!
     *
     */
    override val remoteVersion: KotlinVersion
        // Might be the same version as local version until a remote version number is available: increment
        get() = KotlinVersion(1, 5, 3)

    /**
     * Information about ColabFold component
     */
    override val componentInfo: ComponentInfo
        get() = ComponentInfo(
            aComponentLogoResourceFilepath = "assets/component_logos/colabfold_96_dpi.png",
            aComponentDescription = "Prediction of protein structures and complexes on a local computer",
            anInstallationLocation = PathDefinitions.LOCAL_COLABFOLD_DIR
        )

    override var states: ComponentState = ComponentState(
        isInstalled(),
        hasUpdate()
    )
    //</editor-fold>

    /**
     * Install ColabFold component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override suspend fun install(): Boolean {
        // Check if alma-colabfold-9-rootfs.tar exists
        val tmpDir: File = File("${PathDefinitions.PYSSA_INSTALLER_PROGRAM_DIR}\\temp")
        val tmpTarFile: File = File("${tmpDir}\\alma-colabfold-9-rootfs.tar")
        if (!tmpDir.exists()) {
            tmpDir.mkdirs()
        }
        // First check if anything is stored offline
        if (!tmpTarFile.exists()) {
            // Nothing found, download needed
            if (Utils.isInternetAvailable()) {
                fileLogger.append(LogLevel.INFO, "Downloading almalinux9 distro ...")
                downloadFile(UrlDefinitions.COLABFOLD_ROOTFS_TAR, tmpTarFile.absolutePath)
                fileLogger.append(LogLevel.INFO, "Downloading almalinux9 distro finished.")
            } else {
                return false
            }
        }
        // Create necessary directories if they don't exist
        val tmpDirectoriesToCreate: Array<File> = arrayOf(
            File(PathDefinitions.LOCAL_COLABFOLD_DIR),
            File("${PathDefinitions.LOCAL_COLABFOLD_DIR}\\scripts\\"),
            File("${PathDefinitions.LOCAL_COLABFOLD_DIR}\\storage\\")
        )

        for (tmpDirectory in tmpDirectoriesToCreate) {
            if (!tmpDirectory.exists()) {
                tmpDirectory.mkdirs()
            }
        }
        fileLogger.append(LogLevel.INFO, "Start AlmaLinux9 distribution import.")

        try {
            communicator.startWindowsWrapper(false)
            delay(3000)
            val tmpData = RequestData(
                OperationTypeDefinitions.RUN_CMD_COMMAND,
                arrayOf("wsl --import almaColabfold9 C:\\ProgramData\\localcolabfold\\storage C:\\ProgramData\\IBCI\\PySSA-Installer\\temp\\alma-colabfold-9-rootfs.tar")
            )
            if (!tmpData.writeToJsonFile()) {
                fileLogger.append(LogLevel.ERROR, "Writing data to json file failed!")
                stopCommunicator()
                return false
            }
            fileLogger.append(LogLevel.INFO, "Sending request to: Importing the AlmaLinux distribution ...")
            if (!communicator.sendRequest(PathDefinitions.EXCHANGE_JSON)) {
                fileLogger.append(LogLevel.ERROR, "Importing the AlmaLinux distribution with the Windows wrapper failed!")
                stopCommunicator()
                return false
            } else {
                fileLogger.append(LogLevel.DEBUG, communicator.lastReply)
            }
            stopCommunicator()
            File(tmpTarFile.absolutePath).deleteRecursively()
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            stopCommunicator()
            return false
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

    /**
     * Uninstall ColabFold component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override suspend fun uninstall(): Boolean {
        fileLogger.append(LogLevel.INFO, "Start Colabfold uninstall.")
        try {
            val tmpData = RequestData(
                OperationTypeDefinitions.RUN_CMD_COMMAND,
                arrayOf("wsl --unregister almaColabfold9")
            )
            if (!tmpData.writeToJsonFile()) {
                fileLogger.append(LogLevel.ERROR, "Writing data to json file failed!")
                stopCommunicator()
                return false
            }
            fileLogger.append(LogLevel.INFO, "Sending request to: Importing the AlmaLinux distribution ...")
            communicator.startWindowsWrapper(false)
            delay(3000)
            if (!communicator.sendRequest(PathDefinitions.EXCHANGE_JSON)) {
                fileLogger.append(LogLevel.ERROR, "Importing the AlmaLinux distribution with the Windows wrapper failed!")
                stopCommunicator()
                return false
            } else {
                fileLogger.append(LogLevel.DEBUG, communicator.lastReply)
                val localColabfoldPath: File = File(PathDefinitions.LOCAL_COLABFOLD_DIR)
                if (!File(PathDefinitions.LOCAL_COLABFOLD_STORAGE_VDHX).exists()) {
                    if (localColabfoldPath.exists()) {
                        localColabfoldPath.deleteRecursively()
                    }
                }
                stopCommunicator()
                return true
            }
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            stopCommunicator()
            return false
        }
    }

    /**
     * Update ColabFold component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override suspend fun update(aSystemState: State<List<Boolean>>): Boolean {
        return true
    }

    /**
     * Checks if the component is installed
     *
     * @return True if component is installed, false: Otherwise
     */
    override fun isInstalled(): Boolean {
        return File(PathDefinitions.LOCAL_COLABFOLD_STORAGE_VDHX).exists()
    }

    /**
     * Checks if the component has an update
     *
     * @return True if component has update, false: Otherwise
     */
    override fun hasUpdate(): Boolean {
        // Note: A better implementation is done in another installer update
        return false // Always returns false, because updates are currently not in planning
    }

    /**
     * Checks if all prerequisite are met for an installation
     *
     * @return True if component can be installed, false: Otherwise
     */
    override fun checkPrerequisitesForInstallation(aSystemState: State<List<Boolean>>): Boolean {
        // 2 -> PySSA, 0 -> WSL2
        if (!aSystemState.value[2] && aSystemState.value[0]) {
            val tmpTarFile: File = File(PathDefinitions.PYSSA_INSTALLER_ALMALINUX_TAR)
            if (tmpTarFile.exists()) {
                return true
            }
            if (Utils.isInternetAvailable()) {
                return true
            }
            return false
        } else {
            return false
        }
//        val tmpWslComponent: WslComponent = WslComponent()
//        val tmpPyssaComponent: PyssaComponent = PyssaComponent()
//        if (!tmpPyssaComponent.states.isInstalled.value && tmpWslComponent.states.isInstalled.value) {
//            return true
//        } else {
//            return false
//        }
    }

    /**
     * Checks if all prerequisite are met for an uninstallation
     *
     * @return True if component can be uninstalled, false: Otherwise
     */
    override fun checkPrerequisitesForUninstallation(aSystemState: State<List<Boolean>>): Boolean {
        if (!aSystemState.value[2] && aSystemState.value[0]) {
            return true
        } else {
            return false
        }
    }
}
