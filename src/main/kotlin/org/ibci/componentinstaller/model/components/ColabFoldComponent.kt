package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.mutableStateOf
import org.ibci.componentinstaller.model.util.definitions.ComponentDefinitions
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
     * Install ColabFold component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    // fixme: If it needs to run asynchronously use suspend after fun!
    override fun install(): Boolean {
        // Check if alma-colabfold-9-rootfs.tar exists
        // TODO: Work with a flag, if a new .tar file exists!
        if (!File("C:\\ProgramData\\pyssa-installer\\temp\\alma-colabfold-9-rootfs.tar").exists()) {
            fileLogger.append(LogLevel.ERROR, "alma-colabfold-9-rootfs.tar could not be found.")
            return false
        }

        // Create necessary directories if they don't exist
        val directoriesToCreate = listOf(
            "C:\\ProgramData\\localcolabfold\\",
            "C:\\ProgramData\\localcolabfold\\scripts\\",
            "C:\\ProgramData\\localcolabfold\\storage\\"
        )
        directoriesToCreate.forEach { directory ->
            if (directory.notExists()) {
                directory.createDirectories()
            }
        }
        fileLogger.append(LogLevel.INFO, "Start AlmaLinux9 distribution import.")

        try {
            val process = ProcessBuilder("cmd.exe", "/C", "wsl", "--import", "almaColabfold9",
                "C:\\ProgramData\\localcolabfold\\storage",
                "C:\\ProgramData\\pyssa-installer\\temp\\alma-colabfold-9-rootfs.tar"
            ).apply {
                redirectErrorStream(true)
                directory(File(System.getProperty("user.home")))
            }.start()

            process.waitFor()

            fileLogger.append(LogLevel.ERROR, "WSL2 command finished without errors. Colabfold successfully imported.")
        }
        catch (ex: Exception) {
            fileLogger.append(LogLevel.ERROR, "Process ended with error: $ex")
            return false
        }
        return true
    }
    }

    /**
     * Uninstall ColabFold component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun uninstall(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Update ColabFold component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun update(): Boolean {
        TODO("Not yet implemented")
    }
}
