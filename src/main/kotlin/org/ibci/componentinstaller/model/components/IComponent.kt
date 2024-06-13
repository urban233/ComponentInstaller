package org.ibci.componentinstaller.model.components

/**
 * Interface for components
 *
 */
interface IComponent {
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
     * Checks if a component is installed
     *
     * @return True if component is installed, false: Otherwise
     */
    fun isInstalled(): Boolean

    /**
     * Checks if a component has an update
     *
     * @return True if component has update, false: Otherwise
     */
    fun hasUpdate(): Boolean
}