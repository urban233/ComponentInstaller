package org.ibci.componentinstaller.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ibci.componentinstaller.gui.GuiDefinitions
import org.ibci.componentinstaller.gui.composables.ComposableCollection
import org.ibci.componentinstaller.gui.composables.ComposableCollection.moreOptionsMenu
import org.ibci.componentinstaller.gui.composables.DialogComposable
import org.ibci.componentinstaller.gui.composables.LowLevelComposable
import org.ibci.componentinstaller.model.components.ColabFoldComponent
import org.ibci.componentinstaller.model.components.ExampleComponent
import org.ibci.componentinstaller.model.components.PyssaComponent
import org.ibci.componentinstaller.model.components.WslComponent
import org.ibci.componentinstaller.model.util.VersionHelper
import org.ibci.componentinstaller.model.util.VersionHistory
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel

/**
 * Describes the outline of the main window
 */
@Composable
fun MainWindow(aController: MainWindowController) {
    val tmpFileLogger = FileLogger()
    tmpFileLogger.append(LogLevel.DEBUG, "Running composable function: MainWindow")
    val scrollState = rememberScrollState()
    val components = remember { mutableStateListOf(
        WslComponent(),
        ColabFoldComponent(),
        PyssaComponent()
    ) }
    val systemState = derivedStateOf {
        components.map { component -> component.states.isInstalled.value }
    }
    // These components are useful for debug purposes
//    val components = remember { mutableStateListOf(
//        ExampleComponent("WSL2"),
//        ExampleComponent("ColabFold"),
//        ExampleComponent("PySSA")
//    ) }
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
                    if (tmpComponent.states.isInstalled.value) {
                        ComposableCollection.componentItem(
                            tmpComponent,
                            aController,
                            isJobRunning,
                            systemState
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
                    if (!tmpComponent.states.isInstalled.value) {
                        ComposableCollection.componentItem(
                            tmpComponent,
                            aController,
                            isJobRunning,
                            systemState
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
    val moreOptionsExpanded = remember { mutableStateOf(false) }
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
        Spacer(modifier = Modifier.weight(1f)) // Spacer to push button to the right
        Box {
            IconButton(
                onClick = { moreOptionsExpanded.value = !moreOptionsExpanded.value },
                modifier = Modifier
                    .width(48.dp)
                    .height(24.dp)
                    .fillMaxWidth()
                    .pointerHoverIcon(PointerIcon.Hand),
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "More Options",
                    tint = Color.White,
                    modifier = Modifier
                        .width(40.dp)
                        .height(24.dp),
                )
            }
            if (moreOptionsExpanded.value) {
                appMenu(moreOptionsExpanded)
            }
        }
    }
}

/**
 * Describes a menu that is valid for the app
 *
 */
@Composable
fun appMenu(moreOptionsExpanded: MutableState<Boolean>) {
    val aboutDialog = remember { mutableStateOf(false) }
    DropdownMenu(
        expanded = moreOptionsExpanded.value,
        onDismissRequest = { moreOptionsExpanded.value = false },
        modifier = Modifier
            .width(200.dp)
            .background(MaterialTheme.colors.surface)
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
    ) {
        LowLevelComposable.standardDropdownMenuItem(
            onClickFunction = {
                moreOptionsExpanded.value = false
                ComposableCollection.openWindowsExplorer(PathDefinitions.PYSSA_INSTALLER_PROGRAM_DIR)
            },
            aText = "Open Install Location"
        )
        LowLevelComposable.standardDropdownMenuItem(
            onClickFunction = {
                ComposableCollection.openFileWithDefaultApp("C:\\ProgramData\\IBCI\\PySSA-Installer\\ReferenceManualforPySSAComponentInstaller.pdf")
                moreOptionsExpanded.value = false},
            aText = "Help"
        )
        LowLevelComposable.standardDropdownMenuItem(
            onClickFunction = {
                aboutDialog.value = true
            },
            aText = "About"
        )
        if (aboutDialog.value) {
            val localVersionHistory: VersionHistory = VersionHelper.createVersionHistoryFromLocalFile(
                PathDefinitions.PYSSA_INSTALLER_VERSION_HISTORY_JSON
            )
            DialogComposable.CustomDialog(
                onCloseRequest = {
                    aboutDialog.value = false
                    moreOptionsExpanded.value = false },
                title = "About PySSA Component Installer v${localVersionHistory.getLatestVersion().version}",
                content = "PySSA Component Installer is capable of installing all three components needed for launching and using PySSA." +
                        "\n\nMinimum requirements: Windows 10 22H2 - Build: 19045.4529" +
                        "\nRecommended requirements: Windows 11 23H2 - Build: 22631.3737 or newer" +
                        "\nTo check the version open Settings > System > About or press Win + R and enter winver and select OK." +
                        "\nFor more information see Help.",
            )
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
