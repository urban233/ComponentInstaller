package org.ibci.componentinstaller.gui

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.*
import org.ibci.componentinstaller.model.components.IComponent
import org.ibci.componentinstaller.util.logger.FileLogger

class MainWindowController(theDialogStates: DialogStates) {
    /**
     * Class file logger
     */
    val fileLogger: FileLogger = FileLogger()
    /**
     * All states of the different application dialogs that could be managed with the controller
     */
    val states = theDialogStates

    /**
     * Installs a component
     *uninstallComponent
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
    ) {
        val tmpComponentName: String = aComponent.name
        aProgressDescriptionUpdate("Removing component $tmpComponentName ...")
        delay(2000)
        aProgressDescriptionUpdate("Running removing process ...")
        delay(2000)
        aComponent.uninstall()
        aProgressDescriptionUpdate("Finish up removing ...")
        delay(2000)
        aComponent.isInstalled()
    }
}