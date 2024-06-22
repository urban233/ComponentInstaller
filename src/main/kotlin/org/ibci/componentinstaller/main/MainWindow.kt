package org.ibci.componentinstaller.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ibci.componentinstaller.gui.GuiDefinitions
import org.ibci.componentinstaller.gui.composables.ComposableCollection
import org.ibci.componentinstaller.gui.composables.LowLevelComposable
import org.ibci.componentinstaller.model.components.ColabFoldComponent
import org.ibci.componentinstaller.model.components.ExampleComponent
import org.ibci.componentinstaller.model.components.PyssaComponent
import org.ibci.componentinstaller.model.components.WslComponent

/**
 * Describes the outline of the main window
 */
@Composable
fun MainWindow(aController: MainWindowController) {
    val scrollState = rememberScrollState()
    val components = remember { mutableStateListOf(
        WslComponent(),
        ColabFoldComponent(),
        PyssaComponent()
    ) }
    val isAvailableExpanded = remember { mutableStateOf(true) }
    val isJobRunning = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        MainWindowHeader()
        Section(
            title = "Installed",
            addComponents = {
                for (tmpComponent in components) {
                    if (tmpComponent.states.value.isInstalled) {
                        ComposableCollection.componentItem(
                            tmpComponent,
                            aController,
                            isJobRunning
                        )
                    }
                }
            }
        )
        ExpandableSection(
            title = "Available",
            expandedState = isAvailableExpanded,
            addComponents = {
                for (tmpComponent in components) {
                    if (!tmpComponent.states.value.isInstalled) {
                        ComposableCollection.componentItem(
                            tmpComponent,
                            aController,
                            isJobRunning
                        )
                    }
                }
            }
        )
    }
}

/**
 * Describes the top header of the main window
 *
 */
@Composable
fun MainWindowHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(GuiDefinitions.PYSSA_BLUE_COLOR)
            .padding(16.dp) // Padding around the text
            .fillMaxWidth()
    ) {
        Image(
                painter = painterResource("assets/installer_48_dpi.png"),
                contentDescription = "Logo Image",
                modifier = Modifier
                    .size(96.dp)
                    .padding(top = 4.dp)
        )
        LowLevelComposable.standardText(
            aText = "PySSA Component Installer",
            aFontSize = 24.sp,
            aFontColor = Color.White,
            aFontWeight = FontWeight.SemiBold,
        )
    }
}

/**
 * Describes the expand and collapse section
 *
 */
@Composable
fun ExpandableSection(
    title: String,
    expandedState: MutableState<Boolean>,
    addComponents: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedState.value = !expandedState.value }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (expandedState.value) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            LowLevelComposable.standardText(
                aText = title,
                aFontSize = 20.sp,
                aFontColor = Color.Black,
                aModifier = Modifier.padding(0.dp)
            )
        }
        if (expandedState.value) {
            addComponents()
        }
    }
}

@Composable
fun Section(
    title: String,
    addComponents: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            LowLevelComposable.standardText(
                aText = title,
                aFontSize = 20.sp,
                aFontColor = Color.Black,
                aModifier = Modifier.padding(0.dp)
            )
        }
        addComponents()
    }
}
