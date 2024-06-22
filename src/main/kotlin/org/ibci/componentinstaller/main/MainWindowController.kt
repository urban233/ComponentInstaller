package org.ibci.componentinstaller.main

import kotlinx.coroutines.*
import org.ibci.componentinstaller.model.components.IComponent
import org.ibci.componentinstaller.util.logger.FileLogger

class MainWindowController() {
    /**
     * Class file logger
     */
    private val fileLogger: FileLogger = FileLogger()

    /**
     * Installs a component
     *
     */
    suspend fun installComponent(
        aComponent: IComponent,
        aProgressDescriptionUpdate: suspend (String) -> Unit
    ) : Boolean {
        aProgressDescriptionUpdate("Starting installation ...")
        delay(2000)
        aProgressDescriptionUpdate("Running installation process ...")
        delay(2000)
        if (aComponent.install()) {
            aProgressDescriptionUpdate("Finish up installation ...")
            delay(2000)
            aComponent.isInstalled()
            return true
        } else {
            return false
        }
    }

    /**
     * Uninstalls a component
     *
     */
    suspend fun uninstallComponent(
        aComponent: IComponent,
        aProgressDescriptionUpdate: suspend (String) -> Unit
    ) : Boolean {
        val tmpComponentName: String = aComponent.name
        aProgressDescriptionUpdate("Removing component $tmpComponentName ...")
        delay(2000)
        aProgressDescriptionUpdate("Running removing process ...")
        delay(2000)
        if (aComponent.uninstall()) {
            aProgressDescriptionUpdate("Finish up removing ...")
            delay(2000)
            aComponent.isInstalled()
            return true
        } else {
            return false
        }
    }

    /**
     * Update a component
     *
     */
    suspend fun updateComponent(
        aComponent: IComponent,
        aProgressDescriptionUpdate: suspend (String) -> Unit
    ) : Boolean {
        val tmpComponentName: String = aComponent.name
        aProgressDescriptionUpdate("Updating component $tmpComponentName ...")
        delay(2000)
        aProgressDescriptionUpdate("Running update process ...")
        delay(2000)
        if(aComponent.update()) {
            aProgressDescriptionUpdate("Finishing up update ...")
            delay(2000)
            aComponent.isInstalled()
            return true
        } else {
            return false
        }
    }
}
