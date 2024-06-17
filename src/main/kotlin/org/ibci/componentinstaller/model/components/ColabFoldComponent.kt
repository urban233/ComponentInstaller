package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.mutableStateOf
import org.ibci.componentinstaller.model.util.CustomProcessBuilder
import org.ibci.componentinstaller.model.util.Io.downloadFile
import org.ibci.componentinstaller.model.util.definitions.ComponentDefinitions
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
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
     * The component name
     *
     */
    override val name: String
        get() = ComponentDefinitions.COMPONENT_NAME_COLABFOLD

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
            aComponentLogoResourceFilepath = "component_logos/colabfold_96_dpi.png",
            aComponentDescription = "An faster AlphaFold protein structure prediction tool"
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
        val tmpTarFile: String = "${PathDefinitions.PYSSA_INSTALLER_PROGRAM_DIR}\\temp\\alma-colabfold-9-rootfs.tar"
        val tmpDestination: String = "${PathDefinitions.PYSSA_INSTALLER_PROGRAM_DIR}\\temp\\alma-colabfold-9-rootfs.tar"
        if (tmpTarFile.isEmpty()) {
            // Download of tar file
            downloadFile(tmpTarFile, tmpDestination)
        }

        // Create necessary directories if they don't exist
        val tmpDirectoriesToCreate: Array<String> = arrayOf(
            PathDefinitions.LOCAL_COLABFOLD_DIR,
            "${PathDefinitions.LOCAL_COLABFOLD_DIR}\\scripts\\",
            "${PathDefinitions.LOCAL_COLABFOLD_DIR}\\storage\\"
        )

        for (tmpDirectory in tmpDirectoriesToCreate) {
            val tmpPath: File = File(tmpDirectory)
            if (!tmpPath.exists()) {
                tmpPath.mkdirs()
            }
        }
        fileLogger.append(LogLevel.INFO, "Start AlmaLinux9 distribution import.")

        try {
            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
            // TODO: Command below needs admin rights!
            tmpCustomProcessBuilder.runCommand(
                arrayOf(
                    "/C", "wsl", "--import", "almaColabfold9",
                    "C:\\ProgramData\\localcolabfold\\storage",
                    "C:\\ProgramData\\pyssa-installer\\temp\\alma-colabfold-9-rootfs.tar"
                )
            )

            fileLogger.append(LogLevel.INFO, "WSL2 command finished without errors. Colabfold successfully imported.")
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
        val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
        // TODO: Command below needs admin rights!
        tmpCustomProcessBuilder.runCommand(
            arrayOf(
                "/C", "wsl --unregister almaColabfold9"
            )
        )

        try {
            fileLogger.append(LogLevel.INFO, "WSL2 command finished without errors.")

            val ext4VhdxPath: File = File("${PathDefinitions.LOCAL_COLABFOLD_DIR}\\storage\\ext4.vhdx")
            val localColabfoldPath: File = File(PathDefinitions.LOCAL_COLABFOLD_DIR)

            if (!ext4VhdxPath.exists()) {
                if (localColabfoldPath.exists()) {
                    localColabfoldPath.deleteRecursively()
                }
            }
            else {
                // Unregistering the WSL2 distro failed therefore return false
                return false
            }
            return true
        }
        catch (ex: Exception) {
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
        fileLogger.append(LogLevel.INFO, "Begin update process by uninstalling ColabFold.")
        if (!uninstall()) {
            fileLogger.append(LogLevel.ERROR, "Update process (subprocess: uninstall()) of ColabFold failed with return false.")
            return false
        }

        fileLogger.append(LogLevel.INFO, "Uninstall of ColabFold was successful, begin with installation of the newest version.")
        if (!install()) {
            fileLogger.append(LogLevel.ERROR, "Update process (subprocess: install()) of ColabFold failed with return code false.")
            return false
        }
        fileLogger.append(LogLevel.INFO, "Update of ColabFold was successful.")
        return true
    }

    /**
     * Checks if the component is installed
     *
     * @return True if component is installed, false: Otherwise
     */
    override fun isInstalled(): Boolean {
        TODO("Not yet implemented")
        // TODO: A routine for discovering that colabfold is installed (look into the code of StateManager in C#)
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
        TODO("Not yet implemented")
        // TODO: Write check for requirements (look into pyssa component for inspiration)
    }

    /**
     * Checks if all prerequisite are met for an uninstallation
     *
     * @return True if component can be uninstalled, false: Otherwise
     */
    override fun checkPrerequisitesForUninstallation(): Boolean {
        TODO("Not yet implemented")
        // TODO: Write check for requirements (look into pyssa component for inspiration)
    }
}
