package org.ibci.componentinstaller.gui.composables

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
            Text(
                text = "PySSA Component Installer",
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif
                )
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
        val showConfirmUninstallDialog = remember { mutableStateOf(false) }
        Row {
            LowLevelComposable.componentNameText(aComponent.name)
            Spacer(modifier = Modifier.weight(1f))
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
                LowLevelComposable.standardButton(
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
                Text(
                    text = aComponent.localVersion.toString(),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 6.dp)
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
                        Text(
                            text = "Update available!",
                            fontSize = 13.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                    // Shows new version number
                    Text(
                        text = aComponent.remoteVersion.toString(),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
            else {
                // Shows a component description
                Text(
                    text = "This component can do cool things.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            if (aJobIsRunningState.value) {
                // Shows progress indicator
                LinearProgressIndicator(
                    color = GuiDefinitions.PYSSA_BLUE_COLOR,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
            }
        }
    }
}
