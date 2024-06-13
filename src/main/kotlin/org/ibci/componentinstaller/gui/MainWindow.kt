package org.ibci.componentinstaller.gui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainWindow() {

    /**
     * Describes the outline of the main window
     */
    @Composable
    fun MainFrame() {
        var installedExpanded by remember { mutableStateOf(true) }
        var availableExpanded by remember { mutableStateOf(true) }
        var installedComponents: SnapshotStateList<String> = remember { mutableStateListOf("", "", "") }
        var availableComponents: SnapshotStateList<String> = remember { mutableStateListOf("WSL2", "ColabFold", "PySSA") }
        val controller = remember { mutableStateOf(MainWindowController(installedComponents)) }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ExpandableSection(
                title = "Installed",
                expanded = installedExpanded,
                onExpandChanged = { installedExpanded = it }
            ) {
                for (tmpInstalledComponent in installedComponents) {
                    ComponentItem(name = tmpInstalledComponent, version = "2022.1.2", aController = controller.value)
                }
            }

            ExpandableSection(
                title = "Available",
                expanded = availableExpanded,
                onExpandChanged = { availableExpanded = it }
            ) {
                for (tmpAvailableComponent in availableComponents) {
                    ComponentItem(name = tmpAvailableComponent, aController = controller.value)
                }
            }
        }
    }

    /**
     * Describes the expand and collapse section
     *
     */
    @Composable
    fun ExpandableSection(
        title: String,
        expanded: Boolean,
        onExpandChanged: (Boolean) -> Unit,
        content: @Composable () -> Unit
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandChanged(!expanded) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 20.sp)
            }
            if (expanded) {
                content()
            }
        }
    }

    /**
     * Describes a single component item
     *
     */
    @Composable
    fun ComponentItem(name: String, aController: MainWindowController, version: String? = null, updateAvailable: Boolean = false) {
        if (name != "") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(GuiDefinitions.COMPONENT_BACKGROUND_COLOR, shape = MaterialTheme.shapes.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(name, fontSize = 16.sp)
                    if (version != null) {
                        Text(version, fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (updateAvailable) {
                    TextButton(onClick = { /* Handle update */ }) {
                        Text("Update")
                    }
                } else {
                    TextButton(onClick = {
                        aController.installComponent(name)
                    }) {
                        Text("Install")
                    }
                }
            }
        }
    }
}