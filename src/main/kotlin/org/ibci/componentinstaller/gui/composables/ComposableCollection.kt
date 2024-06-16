package org.ibci.componentinstaller.gui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.ibci.componentinstaller.gui.GuiDefinitions
import org.ibci.componentinstaller.gui.MainWindowController
import org.ibci.componentinstaller.model.components.IComponent
import org.ibci.componentinstaller.util.CustomFonts

/**
 * Object for storing high-level composable functions
 */
object ComposableCollection {
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
                    painter = painterResource("installer_48_dpi.png"),
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
     * Describes a single component item
     *
     */
    @Composable
    fun ComponentItem(
        aComponent: IComponent,
        aController: MainWindowController
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        val jobIsRunning = remember { mutableStateOf(false) }
        val isUpdatable: MutableState<Boolean> = remember { mutableStateOf<Boolean>(aComponent.hasUpdate()) }
        val isInstalled: MutableState<Boolean> = remember { mutableStateOf<Boolean>(aComponent.isInstalled()) }

        Row(
            modifier = Modifier
                .background(if (isHovered) GuiDefinitions.COMPONENT_HOVER_BACKGROUND_COLOR else GuiDefinitions.COMPONENT_BACKGROUND_COLOR, shape = MaterialTheme.shapes.medium)
                .hoverable(interactionSource = interactionSource)
        ) {
            LowLevelComposable.componentLogo("component_logos/wsl_96_dpi.png")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    ComponentActions(
                        aComponent = aComponent,
                        aController = aController,
                        anIsInstalledState = isInstalled,
                        anUpdateAvailableState = isUpdatable,
                        aJobIsRunningState = jobIsRunning
                    )
                    ComponentInformation(
                        aComponent = aComponent,
                        anIsInstalledState = isInstalled,
                        anUpdateAvailableState = isUpdatable,
                        aJobIsRunningState = jobIsRunning
                    )
                    DialogComposable.ConfirmUninstallDialog(aController)
                    DialogComposable.AboutDialog(aController)
                }
            }
        }
    }

    /**
     * Describes the component name, "Install/Update" and "More" button
     *
     */
    @Composable
    fun ComponentActions(
        aComponent: IComponent,
        aController: MainWindowController,
        anIsInstalledState: MutableState<Boolean>,
        anUpdateAvailableState: MutableState<Boolean>,
        aJobIsRunningState: MutableState<Boolean>
    ) {
        val coroutineScope = rememberCoroutineScope()
        // State variable for the expand section of the more options functionality
        val moreOptionsExpanded = remember { mutableStateOf(false) }
        Row {
            LowLevelComposable.componentNameText(aComponent.name)
            Spacer(modifier = Modifier.weight(1f)) // Spacer to push button to the right
            if (anIsInstalledState.value) {
                if (anUpdateAvailableState.value) {
                    LowLevelComposable.standardButton(
                        onClickFunction = {
                            coroutineScope.launch {
                                aController.installComponent(aComponent, aJobIsRunningState)
                                anIsInstalledState.value = aComponent.isInstalled()
                            }
                        },
                        aText = "Update",
                        isEnabled = !aJobIsRunningState.value
                    )
                }
            } else {
                LowLevelComposable.outlinedStandardButton(
                    onClickFunction = {
                        coroutineScope.launch {
                            aController.installComponent(aComponent, aJobIsRunningState)
                            anIsInstalledState.value = aComponent.isInstalled()
                        }
                    },
                    aText = "Install",
                    isEnabled = !aJobIsRunningState.value
                )
            }
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
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        modifier = Modifier
                            .width(40.dp)
                            .height(24.dp),
                    )
                }
                DropdownMenu(
                    expanded = moreOptionsExpanded.value,
                    onDismissRequest = { moreOptionsExpanded.value = false }
                ) {
                    if (anIsInstalledState.value) {
                        DropdownMenuItem(onClick = { /* handle dismiss */ }) {
                            Text("Install Location")
                        }
                        DropdownMenuItem(onClick = { aController.states.confirmUninstallDialogVisible = true }) {
                            Text(
                                text = "Uninstall",
                                color = GuiDefinitions.COMPONENT_UNINSTALL_COLOR
                            )
                        }
                    } else {
                        DropdownMenuItem(onClick = { aController.states.aboutDialogVisible = true }) {
                            Text(
                                text = "About"
                            )
                        }
                    }

                }
            }
            Spacer(modifier = Modifier.width(1.dp))
        }
    }

    /**
     * Describes the text below the component name (including the progress indication)
     *
     */
    @Composable
    fun ComponentInformation(aComponent: IComponent,
                             anIsInstalledState: MutableState<Boolean>,
                             anUpdateAvailableState: MutableState<Boolean>,
                             aJobIsRunningState: MutableState<Boolean>) {
        Column {
            if (anIsInstalledState.value) {
                // Shows installed version
                LowLevelComposable.standardText(
                    aText = aComponent.localVersion.toString()
                )
                if (anUpdateAvailableState.value) {
                    // Shows update icon and text
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon (
                            imageVector = Icons.Default.Info,
                            contentDescription = "Update info icon",
                            tint = GuiDefinitions.COMPONENT_INFO_ICON_COLOR,
                            modifier = Modifier.size(24.dp)
                        )
                        LowLevelComposable.standardText(
                            aText = "Update available!",
                            aFontSize = 13.sp,
                            aFontColor = Color.Black,
                            aFontWeight = FontWeight.SemiBold,
                            aModifier = Modifier
                                .padding(bottom = 4.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                    // Shows new version number
                    LowLevelComposable.standardText(
                        aText = aComponent.remoteVersion.toString()
                    )
                }
            }
            else {
                // Shows a component description
                LowLevelComposable.standardText(
                    aText = "This component can do cool things."
                )
            }

            if (aJobIsRunningState.value) {
                ProgressIndication()
            }
        }
    }

    /**
     * Describes the progress indication with text, progress bar and cancel button
     *
     */
    @Composable
    fun ProgressIndication() {
        // Complete container
        Column {
            // Container for the text and cancel button
            Row (verticalAlignment = Alignment.CenterVertically) {
                // Progress description
                LowLevelComposable.standardText(
                    aText = "Job is running ..."
                )
                Spacer(modifier = Modifier.weight(1f)) // Spacer to push button to the right
                // Cancel button
                TextButton(
                    onClick = { /* needs logic */ },
                    modifier = Modifier
                        .width(100.dp)
                        .height(24.dp)
                        .fillMaxWidth()
                        .pointerHoverIcon(PointerIcon.Hand),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    LowLevelComposable.textForButton(
                        aText = "Cancel",
                        aColor = GuiDefinitions.PYSSA_BLUE_COLOR,
                        aFontWeight = FontWeight.SemiBold
                    )
                }
            }
            // Progress bar
            LinearProgressIndicator(
                color = GuiDefinitions.PYSSA_BLUE_COLOR,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
        }

    }
}
