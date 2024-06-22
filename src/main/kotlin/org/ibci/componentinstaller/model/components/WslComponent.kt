package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.Job
import org.ibci.componentinstaller.gui.ComponentState
import org.ibci.componentinstaller.model.util.CustomProcessBuilder
import org.ibci.componentinstaller.model.util.definitions.ComponentDefinitions
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel
import java.io.File

class WslComponent : IComponent {
    //<editor-fold desc="Class attributes">
    /**
     * File logger of this class
     */
    private val fileLogger: FileLogger = FileLogger()
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

    override var states: MutableState<ComponentState> = mutableStateOf(
        ComponentState(
            isInstalled = isInstalled(),
            isUpdatable = hasUpdate(),
            componentJob = Job(),
            isComponentJobRunning = false
        )
    )

    /**
     * The installation state
     *
     * This should be seen as a private property, therefore the leading underscore.
     */
    override var installedState: MutableState<Boolean> = mutableStateOf(false)

    /**
     * The update state
     *
     * This should be seen as a private property, therefore the leading underscore.
     */
    override var updatableState: MutableState<Boolean> = mutableStateOf(false)
    //</editor-fold>

    init {
        installedState.value = isInstalled()
        updatableState.value = hasUpdate()
    }

    /**
     * Install WSL2 component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun install(): Boolean {
        try {
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            tmpCustomProcessBuilder.runCommand(
                anExecutable = PathDefinitions.CMD_ELEVATOR_EXE,
                aCommand = arrayOf("cmd.exe", "/C", "wsl", "--install", "--no-distribution")
            )
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, ex.toString())
            return false
        }
    }

    /**
     * Uninstall WSL2 component
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    override fun uninstall(): Boolean {
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
    override fun update(): Boolean {
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
        val tmpProcessBuilder: ProcessBuilder = ProcessBuilder(PathDefinitions.CHECK_WSL_BAT)
        val tmpProcess = tmpProcessBuilder.start()
        tmpProcess.waitFor()
        val tmpFile = File(PathDefinitions.WSL_INSTALLED)
        if (tmpFile.exists()) {
            tmpFile.delete()
            return true
        } else {
            return false
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
    override fun checkPrerequisitesForInstallation(): Boolean {
        val tmpColabfoldComponent: ColabFoldComponent = ColabFoldComponent()
        val tmpPyssaComponent: PyssaComponent = PyssaComponent()
        if (!tmpPyssaComponent.states.component1().isInstalled && !tmpColabfoldComponent.states.component1().isInstalled) {
            return true
        } else {
            return false
        }
    }

    /**
     * Checks if all prerequisite are met for an uninstallation
     *
     * @return True if component can be uninstalled, false: Otherwise
     */
    override fun checkPrerequisitesForUninstallation(): Boolean {
        return false
//        val tmpColabfoldComponent: ColabFoldComponent = ColabFoldComponent()
//        val tmpPyssaComponent: PyssaComponent = PyssaComponent()
//        if (!tmpPyssaComponent.isInstalled() && !tmpColabfoldComponent.isInstalled()) {
//            return true
//        } else {
//            return false
//        }
    }
}
