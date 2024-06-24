package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.delay
import org.ibci.componentinstaller.gui.ComponentState
import org.ibci.componentinstaller.model.util.CustomProcessBuilder
import org.ibci.componentinstaller.model.util.Io
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

class WslComponent : IComponent {
    //<editor-fold desc="Class attributes">
    /**
     * File logger of this class
     */
    private val fileLogger: FileLogger = FileLogger()
    /**
     * The communicator for the windows wrapper
     *
     */
    private val communicator = Communicator()
    /**
     * The component name
     */
    override val name: String
        get() = ComponentDefinitions.COMPONENT_NAME_WSL2

    /**
     * The local component version
     * IMPORTANT: If version could not be found, the major is -1!!!
     */
    override val localVersion: KotlinVersion
        get() = KotlinVersion(2, 2, 4) // This version number is NOT constant and needs to be checked

    /**
     * The remote component version
     * IMPORTANT: If version could not be found, the major is -1!!!
     */
    override val remoteVersion: KotlinVersion
        get() = KotlinVersion(1, 0, 0) // This is not necessary because the WSL is updated through windows

    /**
     * Information about the component
     */
    override val componentInfo: ComponentInfo = ComponentInfo(
        aComponentLogoResourceFilepath = "assets/component_logos/wsl_96_dpi.png",
        aComponentDescription = "Enables running a Linux kernel inside a lightweight virtual machine",
        anInstallationLocation = ""
    )

    override var states: ComponentState = ComponentState(
        isInstalled(),
        hasUpdate()
    )
    //</editor-fold>

    /**
     * Install WSL2 component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override suspend fun install(): Boolean {
        try {
            if (!Utils.isInternetAvailable()) {
                return false
            }
            communicator.startWindowsWrapper(true)
            delay(1500)
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            tmpCustomProcessBuilder.openCommand(arrayOf(""), anExecutable = PathDefinitions.WINDOW_HIDE_EXE)
            delay(3000)
            val tmpData = RequestData(
                OperationTypeDefinitions.RUN_CMD_COMMAND,
                arrayOf("wsl --install --no-distribution")
            )
            if (!tmpData.writeToJsonFile()) {
                fileLogger.append(LogLevel.ERROR, "Writing data to json file failed!")
                stopCommunicator()
                return false
            }
            fileLogger.append(LogLevel.INFO, "Sending request to: Install WSL2 without any distro Windows shortcuts ...")
            if (!communicator.sendRequest(PathDefinitions.EXCHANGE_JSON)) {
                fileLogger.append(LogLevel.ERROR, "Installing WSL2 without any distro with the Windows wrapper failed!")
                stopCommunicator()
                if (!alternativeInstall()) {
                    return false
                }
            } else {
                fileLogger.append(LogLevel.DEBUG, communicator.lastReply)
            }
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "$ex")
            stopCommunicator()
            return false
        }
        stopCommunicator()
        return true
    }

    /**
     * An alternative installation way of WSL2
     *
     */
    fun alternativeInstall() : Boolean {
        try {
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            if (File(PathDefinitions.WSL_MANUAL_INSTALLER_MSI).exists()) {
                // System after reboot
                tmpCustomProcessBuilder.runCommand(
                    anExecutable = PathDefinitions.CMD_ELEVATOR_EXE,
                    aCommand = arrayOf("cmd.exe", "/C", PathDefinitions.WSL_MANUAL_INSTALLER_MSI)
                )
                tmpCustomProcessBuilder.openCommand(arrayOf(""), anExecutable = PathDefinitions.WINDOW_HIDE_EXE)
                File(PathDefinitions.WSL_MANUAL_INSTALLER_MSI).delete()
            } else {
                // Download msi installer and enable features first, then restart
                Io.downloadFile(UrlDefinitions.WSL_MANUAL_MSI_INSTALLER, PathDefinitions.WSL_MANUAL_INSTALLER_MSI)
                tmpCustomProcessBuilder.runCommand(
                    anExecutable = PathDefinitions.CMD_ELEVATOR_EXE,
                    aCommand = arrayOf("cmd.exe", "/C", "dism.exe", "/Online /Enable-Feature /FeatureName:VirtualMachinePlatform /NoRestart")
                )
                tmpCustomProcessBuilder.openCommand(arrayOf(""), anExecutable = PathDefinitions.WINDOW_HIDE_EXE)
                tmpCustomProcessBuilder.runCommand(
                    anExecutable = PathDefinitions.CMD_ELEVATOR_EXE,
                    aCommand = arrayOf("cmd.exe", "/C", "dism.exe", "/Online /Enable-Feature /FeatureName:Microsoft-Windows-Subsystem-Linux /Restart")
                )
                tmpCustomProcessBuilder.openCommand(arrayOf(""), anExecutable = PathDefinitions.WINDOW_HIDE_EXE)
            }
        } catch (ex: Exception) {
            return false
        }
        return true
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
     * Uninstall WSL2 component NOT NECESSARY ANYMORE!!!
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    override suspend fun uninstall(): Boolean {
        try {
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            tmpCustomProcessBuilder.runCommand(
                anExecutable = PathDefinitions.CMD_ELEVATOR_EXE,
                aCommand = arrayOf("cmd.exe", "/C", "dism.exe", "/Online /Disable-Feature /FeatureName:VirtualMachinePlatform /NoRestart")
            )
            tmpCustomProcessBuilder.runCommand(
                anExecutable = PathDefinitions.CMD_ELEVATOR_EXE,
                aCommand = arrayOf("cmd.exe", "/C", "dism.exe", "/Online /Disable-Feature /FeatureName:Microsoft-Windows-Subsystem-Linux /NoRestart")
            )
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, ex.toString())
            return false
        }
    }

    /**
     * Update WSL2 component
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    override suspend fun update(aSystemState: State<List<Boolean>>): Boolean {
        // The update process is handled by the OS and the internal update state is always false,
        // therefore true is returned
        return true
    }

    /**
     * Checks if the WSL2 component is installed
     *
     * @return True if component is installed, false: Otherwise
     */
    override fun isInstalled(): Boolean {
        val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
        tmpCustomProcessBuilder.openCommand(arrayOf(""), anExecutable = PathDefinitions.WINDOW_HIDE_EXE)
        communicator.startWindowsWrapper(true)
        val tmpData = RequestData(
            OperationTypeDefinitions.CHECK_WSL_INSTALLATION,
            arrayOf("Check WSL installation.")
        )
        if (!tmpData.writeToJsonFile()) {
            fileLogger.append(LogLevel.ERROR, "Writing data to json file failed!")
            stopCommunicator()
            return false
        }
        fileLogger.append(LogLevel.INFO, "Sending request to: Check WSL2 installation ...")
        if (!communicator.sendRequest(PathDefinitions.EXCHANGE_JSON)) {
            fileLogger.append(LogLevel.ERROR, "Check WSL2 installation failed!")
            stopCommunicator()
            return false
        } else {
            fileLogger.append(LogLevel.DEBUG, communicator.lastReply)
            if (communicator.lastReply == "Is installed.") {
                stopCommunicator()
                if (File(PathDefinitions.WSL_MANUAL_INSTALLER_MSI).exists()) {
                    return false
                }
                return true
            } else {
                stopCommunicator()
                return false
            }
        }
    }

    /**
     * Checks if the WSL2 component has an update
     *
     * @return True if component has update, false: Otherwise
     */
    override fun hasUpdate(): Boolean {
        // The update is handled by the OS, therefore the state is always false
        return false
    }

    /**
     * Checks if all prerequisite are met for an installation
     *
     * @return True if component can be installed, false: Otherwise
     */
    override fun checkPrerequisitesForInstallation(aSystemState: State<List<Boolean>>): Boolean {
        // 2 -> PySSA, 1 -> ColabFold
        if (!aSystemState.value[2] && !aSystemState.value[1]) {
            return Utils.isInternetAvailable()
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
        return false
    }
}
