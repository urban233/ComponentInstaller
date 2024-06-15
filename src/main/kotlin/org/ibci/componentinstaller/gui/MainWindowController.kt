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
     *
     */
    suspend fun installComponent(
        aComponent: IComponent,
        aJobIsRunningState: MutableState<Boolean>
    ) {
        aJobIsRunningState.value = true
        delay(4000)
        aComponent.install()
        aComponent.setInstalled(true)

//        for (tmpComponent in installedComponents) {
//            if (aComponent == tmpComponent) {
//                installedComponents.remove(aComponent)
//            }
//        }

        aJobIsRunningState.value = false

//        // TODO: Add component object install method inside the if-statement
//        if (aComponent.install()) {
//            // Add component to installed section
//            this.installedComponents.add(aComponent.name)
//        } else {
//            // Add component to available section
//        }
    }
}