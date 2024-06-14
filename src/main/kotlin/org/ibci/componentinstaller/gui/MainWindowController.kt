package org.ibci.componentinstaller.gui

import androidx.compose.runtime.snapshots.SnapshotStateList
import org.ibci.componentinstaller.model.components.IComponent

class MainWindowController(anInstalledComponentsList: SnapshotStateList<String>) {

    private var installedComponents: SnapshotStateList<String> = anInstalledComponentsList

    /**
     * Installs a component
     *
     */
    fun installComponent(aComponent: String) {
        this.installedComponents.add(aComponent.uppercase())

//        // TODO: Add component object install method inside the if-statement
//        if (aComponent.install()) {
//            // Add component to installed section
//            this.installedComponents.add(aComponent.name)
//        } else {
//            // Add component to available section
//        }
    }
}