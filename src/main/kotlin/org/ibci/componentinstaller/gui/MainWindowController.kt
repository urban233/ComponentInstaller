package org.ibci.componentinstaller.gui

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.*

class MainWindowController(theMainWindowStates: MainWindowStates) {
    /**
     * All states of the main window that could be managed with the controller
     */
    val states = theMainWindowStates
    /**
     * Installs a component
     *
     */
    suspend fun installComponent(aComponent: String, aJobIsRunningState: MutableState<Boolean>) {
        aJobIsRunningState.value = true
        delay(4000)
        this.states.installedComponents.add(aComponent.uppercase())
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