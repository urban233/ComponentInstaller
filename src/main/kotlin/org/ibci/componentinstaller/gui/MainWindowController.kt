package org.ibci.componentinstaller.gui

import androidx.compose.runtime.snapshots.SnapshotStateList

class MainWindowController(anInstalledComponentsList: SnapshotStateList<String>) {

    private var installedComponents: SnapshotStateList<String> = anInstalledComponentsList

    /**
     * Installs a component
     *
     */
    fun installComponent(aComponent: String) {
        // TODO: Add component object install method inside the if-statement
        if (true) {
            // Add component to installed section
            this.installedComponents.add(aComponent.uppercase())
        } else {
            // Add component to available section
        }
    }
}