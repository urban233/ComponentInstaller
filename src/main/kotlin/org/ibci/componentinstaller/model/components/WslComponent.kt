package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
        get() = KotlinVersion(1, 0, 0) // TODO: Check version number of WSL

    /**
     * The remote component version
     * IMPORTANT: If version could not be found, the major is -1!!!
     */
    override val remoteVersion: KotlinVersion
        get() = KotlinVersion(1, 0, 0) // This is not necessary because the WSL is updated through windows

    /**
     * Information about the component
     */
    override val componentInfo: ComponentInfo
        get() = ComponentInfo(
            aComponentLogoResourceFilepath = "component_logos/wsl_96_dpi.png",
            aComponentDescription = "Enables running a Linux kernel inside a lightweight virtual machine"
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

    /**
     * Install a component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun install(): Boolean {
        try {
            val customProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            // TODO: Change the command below so it is executed as admin!
            customProcessBuilder.runCommand(arrayOf("/c", "wsl", "--install", "--no-distribution"))
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, ex.toString())
            return false
        }
    }

    /**
     * Uninstall a component
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    override fun uninstall(): Boolean {
        try {
            val customProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            // TODO: Change the commands below so they are run as admin
            customProcessBuilder.runCommand(
                arrayOf("/c", "dism.exe", "/Online /Disable-Feature /FeatureName:VirtualMachinePlatform /NoRestart")
            )
            customProcessBuilder.runCommand(
                arrayOf("/c", "dism.exe", "/Online /Disable-Feature /FeatureName:Microsoft-Windows-Subsystem-Linux /NoRestart")
            )
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, ex.toString())
            return false
        }
    }

    /**
     * Update a component
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    override fun update(): Boolean {
        // The update process is handled by the OS and the internal update state is always false,
        // therefore true is returned
        return true
    }

    /**
     * Checks if the component is installed
     *
     * @return True if component is installed, false: Otherwise
     */
    override fun isInstalled(): Boolean {
        val processBuilder: ProcessBuilder = ProcessBuilder(PathDefinitions.CHECK_WSL_BATCH_SCRIPT_FILEPATH)
        val process = processBuilder.start()
        process.waitFor()
        val tmpFile = File(PathDefinitions.WSL_INSTALLED_FILEPATH)
        if (tmpFile.exists()) {
            tmpFile.delete()
            return true
        } else {
            return false
        }
    }

    /**
     * Checks if the component has an update
     *
     * @return True if component has update, false: Otherwise
     */
    override fun hasUpdate(): Boolean {
        // The update is handled by the OS, therefore the state is always false
        updatableState.value = false
        return updatableState.value
    }

    /**
     * Checks if all prerequisite are met for an installation
     *
     * @return True if component can be installed, false: Otherwise
     */
    override fun checkPrerequisitesForInstallation(): Boolean {
        val colabfoldComponent: ExampleComponent = ExampleComponent("ColabFold") // TODO: Change this if the colabfoldComponent class is available
        val pyssaComponent: PyssaComponent = PyssaComponent()
        if (!pyssaComponent.isInstalled() && !colabfoldComponent.isInstalled()) {
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
        val colabfoldComponent: ExampleComponent = ExampleComponent("ColabFold") // TODO: Change this if the colabfoldComponent class is available
        val pyssaComponent: PyssaComponent = PyssaComponent()
        if (!pyssaComponent.isInstalled() && !colabfoldComponent.isInstalled()) {
            return true
        } else {
            return false
        }
    }
}
