package org.ibci.componentinstaller.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    fun MainFrame() {
        val tmpComposableCollection = ComposableCollection()
        var installedExpanded by remember { mutableStateOf(true) }
        var availableExpanded by remember { mutableStateOf(true) }
        var installedComponents: SnapshotStateList<String> = remember { mutableStateListOf("WSL2", "", "") }
        var availableComponents: SnapshotStateList<String> = remember { mutableStateListOf("ColabFold", "PySSA") }
        val controller = remember { mutableStateOf(MainWindowController(installedComponents)) }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            tmpComposableCollection.MainWindowHeader()

            tmpComposableCollection.ExpandableSection(
                title = "Installed",
                expanded = installedExpanded,
                onExpandChanged = { installedExpanded = it }
            ) {
                for (tmpInstalledComponent in installedComponents) {
                    tmpComposableCollection.ComponentItem(name = tmpInstalledComponent, version = "2022.1.2", aController = controller.value)
                }
            }

            tmpComposableCollection.ExpandableSection(
                title = "Available",
                expanded = availableExpanded,
                onExpandChanged = { availableExpanded = it }
            ) {
                for (tmpAvailableComponent in availableComponents) {
                    tmpComposableCollection.ComponentItem(name = tmpAvailableComponent, aController = controller.value)
                }
            }
        }
    }
}