package org.ibci.componentinstaller.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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