package org.ibci.componentinstaller.gui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ibci.componentinstaller.gui.composables.ComposableCollection

class MainWindow {

    /**
     * Describes the outline of the main window
     */
    @Composable
    fun MainFrame(aController: MainWindowController) {
        val tmpComposableCollection = ComposableCollection()
        val scrollState = rememberScrollState()
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.verticalScroll(scrollState)
            ) {
            tmpComposableCollection.MainWindowHeader()

            tmpComposableCollection.ExpandableSection(
                title = "Installed",
                expanded = aController.states.installedExpanded,
                onExpandChanged = { aController.states.installedExpanded = it }
            ) {
                for (tmpInstalledComponent in aController.states.installedComponents) {
                    tmpComposableCollection.ComponentItem(name = tmpInstalledComponent, version = "2022.1.2", aController = aController)
                }
            }

            tmpComposableCollection.ExpandableSection(
                title = "Available",
                expanded = aController.states.availableExpanded,
                onExpandChanged = { aController.states.availableExpanded = it }
            ) {
                for (tmpAvailableComponent in aController.states.availableComponents) {
                    tmpComposableCollection.ComponentItem(name = tmpAvailableComponent, aController = aController)
                }
            }
        }
    }
}