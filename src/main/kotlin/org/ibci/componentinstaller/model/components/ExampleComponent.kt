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
    override var _updatable = mutableStateOf(true)

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

    init {
        val tmpFile = File("C:\\TEMP\\example_component.isInstalled")
        setInstalled(tmpFile.exists())
        if (isInstalled()) {
            setUpdatable(false) // False is only used as temporary placeholder
        }
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