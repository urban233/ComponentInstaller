package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.ibci.componentinstaller.gui.ComponentState
import org.ibci.componentinstaller.util.logger.FileLogger
import java.io.File

/**
 * Class for an example component
 *
 * This class acts as an example and dummy to integrate the "Component" in the application.
 */
class ExampleComponent(aName: String) : IComponent {
    /**
     * Class file logger
     */
    val fileLogger: FileLogger = FileLogger()

    /**
     * The component name
     *
     */
    override val name: String = aName

    /**
     * The local component version
     *
     */
    override val localVersion: KotlinVersion
        get() = KotlinVersion(0, 1, 0)

    /**
     * The remote component version
     *
     */
    override val remoteVersion: KotlinVersion
        get() = KotlinVersion(0, 1, 0)

    /**
     * Information about the component
     */
    override val componentInfo: ComponentInfo
        get() = ComponentInfo(
            aComponentLogoResourceFilepath = "assets/component_logos/colabfold_96_dpi.png",
            aComponentDescription = "A tool that could be useful",
            anInstallationLocation = ""
        )

    override var states: ComponentState = ComponentState(
        isInstalled(),
        hasUpdate()
    )

    /**
     * Checks if the component is installed
     *
     * @return True if the component is installed, false: Otherwise
     */
    override fun isInstalled() : Boolean {
        val tmpFile = File("C:\\TEMP\\example_component.isInstalled")
        if (tmpFile.exists()) {
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
    override fun hasUpdate() : Boolean {
        if (false) { // TODO: Add the correct logic in the if-statement
            return true
        } else {
            return false
        }
    }

    /**
     * Install a component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override suspend fun install(): Boolean {
        val tmpFile = File("C:\\TEMP\\example_component.isInstalled")
        delay(2000)
        return withContext(Dispatchers.IO) {
            tmpFile.createNewFile()
        }
    }

    /**
     * Uninstall a component
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    override suspend fun uninstall(): Boolean {
        val tmpFile = File("C:\\TEMP\\example_component.isInstalled")
        return tmpFile.delete()
    }

    /**
     * Update a component
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    override suspend fun update(aSystemState: State<List<Boolean>>): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if all prerequisite are met for an installation
     *
     * @return True if component can be installed, false: Otherwise
     */
    override fun checkPrerequisitesForInstallation(aSystemState: State<List<Boolean>>): Boolean {
        return true // TODO: This is only a placeholder!
    }

    /**
     * Checks if all prerequisite are met for an uninstallation
     *
     * @return True if component can be uninstalled, false: Otherwise
     */
    override fun checkPrerequisitesForUninstallation(aSystemState: State<List<Boolean>>): Boolean {
        return true // TODO: This is only a placeholder!
    }
}