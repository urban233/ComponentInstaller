package org.ibci.componentinstaller.gui

import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.*

class MainWindowController(theMainWindowStates: MainWindowStates) {

    var installedComponents: SnapshotStateList<String> = SnapshotStateList()
    val states = theMainWindowStates
    /**
     * Installs a component
     *
     */
    suspend fun installComponent(aComponent: String) {
        delay(2000)
        this.states.installedComponents.add(aComponent.uppercase())

//        // TODO: Add component object install method inside the if-statement
//        if (aComponent.install()) {
//            // Add component to installed section
//            this.installedComponents.add(aComponent.name)
//        } else {
//            // Add component to available section
//        }
    }
}