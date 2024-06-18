package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.mutableStateOf
import org.ibci.componentinstaller.model.util.CustomProcessBuilder
import org.ibci.componentinstaller.model.util.Io.downloadFile
import org.ibci.componentinstaller.model.util.definitions.ComponentDefinitions
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.model.util.definitions.UrlDefinitions
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel
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
            aComponentDescription = "An faster AlphaFold protein structure prediction tool",
            anInstallationLocation = PathDefinitions.LOCAL_COLABFOLD_DIR
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

    /**
     * Install ColabFold component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun install(): Boolean {
        // Check if alma-colabfold-9-rootfs.tar exists
        val tmpTarFile: File = File("${PathDefinitions.PYSSA_INSTALLER_PROGRAM_DIR}\\temp\\alma-colabfold-9-rootfs.tar")
        if (!tmpTarFile.exists()) {
            // Download of tar file
            downloadFile(UrlDefinitions.COLABFOLD_ROOTFS_TAR, tmpTarFile.absolutePath.toString())
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
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            tmpCustomProcessBuilder.runCommand(
                arrayOf(
                    "/C", "runas", "/user:Administrator", "wsl", "--import", "almaColabfold9",
                    "${PathDefinitions.LOCAL_COLABFOLD_DIR}\\storage",
                    tmpTarFile.absolutePath.toString()
                )
            )

            fileLogger.append(LogLevel.INFO, "WSL2 command finished without errors. Colabfold successfully imported.")
            if (isInstalled()){
                return true
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "Process ended with error: $ex")
            return false
        }
    }

    /**
     * Uninstall ColabFold component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun uninstall(): Boolean {
        fileLogger.append(LogLevel.INFO, "Start Colabfold uninstall.")
        try {
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            tmpCustomProcessBuilder.runCommand(arrayOf("/C", "runas", "/user:Administrator", "wsl --unregister almaColabfold9"))
            fileLogger.append(LogLevel.INFO, "WSL2 command finished without errors.")
            val localColabfoldPath: File = File(PathDefinitions.LOCAL_COLABFOLD_DIR)
            if (!File(PathDefinitions.LOCAL_COLABFOLD_STORAGE_VDHX).exists()) {
                if (localColabfoldPath.exists()) {
                    localColabfoldPath.deleteRecursively()
                }
            }
            else {
                // Unregistering the WSL2 distro failed therefore return false
                return false
            }
            return true
        } catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "Process ended with error: $ex")
            return false
        }
    }

    /**
     * Update ColabFold component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun update(): Boolean {
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
    override fun checkPrerequisitesForInstallation(): Boolean {
        val tmpWslComponent: WslComponent = WslComponent()
        val tmpPyssaComponent: PyssaComponent = PyssaComponent()
        if (!tmpPyssaComponent.isInstalled() && tmpWslComponent.isInstalled()) {
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
        val tmpWslComponent: WslComponent = WslComponent()
        val tmpPyssaComponent: PyssaComponent = PyssaComponent()
        if (!tmpPyssaComponent.isInstalled() && tmpWslComponent.isInstalled()) {
            return true
        } else {
            return false
        }
    }
}
