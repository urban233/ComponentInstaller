package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * Interface for components
 *
 */
interface IComponent {
    //<editor-fold desc="Class attributes">
    /**
     * The component name
     */
    val name: String

    /**
     * The local component version
     */
    val localVersion: KotlinVersion

    /**
     * The remote component version
     */
    val remoteVersion: KotlinVersion

    /**
     * The installation state
     *
     * This should be seen as a private property, therefore the leading underscore.
     */
    var _installed: MutableState<Boolean>

    /**
     * The update state
     *
     * This should be seen as a private property, therefore the leading underscore.
     */
    var _updatable: MutableState<Boolean>
    //</editor-fold>

    /**
     * Install a component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    fun install(): Boolean

    /**
     * Uninstall a component
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    fun uninstall(): Boolean

    /**
     * Update a component
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    fun update(): Boolean

    /**
     * Returns the installed state
     *
     * @return True if component is installed, false: Otherwise
     */
    fun isInstalled(): Boolean

    /**
     * Sets the new value for the installed state of the component
     *
     * @param value A boolean that indicated the state of the installation
     */
    fun setInstalled(value: Boolean)

    /**
     * Returns the update state
     *
     * @return True if component has update, false: Otherwise
     */
    fun hasUpdate(): Boolean

    /**
     * Sets the new value for the update state of the component
     *
     * @param value A boolean that indicated the state of the installation
     */
    fun setUpdatable(value: Boolean)
}
