package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.mutableStateOf
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel
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
            aComponentLogoResourceFilepath = "component_logos/colabfold_96_dpi.png",
            aComponentDescription = "A tool that could be useful",
            anInstallationLocation = ""
        )

    /**
     * The installation state
     */
    override var installedState = mutableStateOf(false)

    /**
     * The update state
     */
    override var updatableState = mutableStateOf(false)

    init {
        isInstalled()
        hasUpdate()
    }

    /**
     * Checks if the component is installed
     *
     * @return True if the component is installed, false: Otherwise
     */
    override fun isInstalled() : Boolean {
        val tmpFile = File("C:\\TEMP\\example_component.isInstalled")
        if (tmpFile.exists()) {
            installedState.value = true
        } else {
            installedState.value = false
        }
        return installedState.value
    }

    /**
     * Checks if the component has an update
     *
     * @return True if component has update, false: Otherwise
     */
    override fun hasUpdate() : Boolean {
        if (false) { // TODO: Add the correct logic in the if-statement
            updatableState.value = true
        } else {
            updatableState.value = false
        }
        return updatableState.value
    }

    /**
     * Install a component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun install(): Boolean {
        val tmpFile = File("C:\\TEMP\\example_component.isInstalled")
        return tmpFile.createNewFile()
    }

    /**
     * Uninstall a component
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    override fun uninstall(): Boolean {
        val tmpFile = File("C:\\TEMP\\example_component.isInstalled")
        return tmpFile.delete()
    }

    /**
     * Update a component
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    override fun update(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if all prerequisite are met for an installation
     *
     * @return True if component can be installed, false: Otherwise
     */
    override fun checkPrerequisitesForInstallation(): Boolean {
        return true // TODO: This is only a placeholder!
    }

    /**
     * Checks if all prerequisite are met for an uninstallation
     *
     * @return True if component can be uninstalled, false: Otherwise
     */
    override fun checkPrerequisitesForUninstallation(): Boolean {
        return true // TODO: This is only a placeholder!
    }
}