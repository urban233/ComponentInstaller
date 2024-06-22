package org.ibci.componentinstaller.model.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.ibci.componentinstaller.gui.ComponentState

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
     * Information about the component
     */
    val componentInfo: ComponentInfo

    /**
     * All states related to the component
     */
    var states: ComponentState
    //</editor-fold>

    /**
     * Install a component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    suspend fun install(): Boolean

    /**
     * Uninstall a component
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    suspend fun uninstall(): Boolean

    /**
     * Update a component
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    suspend fun update(aSystemState: State<List<Boolean>>): Boolean

    /**
     * Checks if the component is installed
     *
     * @return True if component is installed, false: Otherwise
     */
    fun isInstalled(): Boolean

    /**
     * Checks if the component has an update
     *
     * @return True if component has update, false: Otherwise
     */
    fun hasUpdate(): Boolean

    /**
     * Checks if all prerequisite are met for an installation
     *
     * @return True if component can be installed, false: Otherwise
     */
    fun checkPrerequisitesForInstallation(aSystemState: State<List<Boolean>>): Boolean

    /**
     * Checks if all prerequisite are met for an uninstallation
     *
     * @return True if component can be uninstalled, false: Otherwise
     */
    fun checkPrerequisitesForUninstallation(aSystemState: State<List<Boolean>>): Boolean
}
