package org.ibci.componentinstaller.gui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
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

/**
 * Class for storing high-level composable functions
 */
class ComposableCollection {
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
    fun ComponentItem(name: String, aController: MainWindowController, version: String? = null) {
        if (name != "") {
            //<editor-fold desc="State variables">
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered by interactionSource.collectIsHoveredAsState()
            // State variable for the update availability of a single component
            val updateAvailable = remember { mutableStateOf(false) }
            // State variable for indicating if a job is running
            val jobIsRunning = remember { mutableStateOf(false) }
            //</editor-fold>
            // Complete row container
            Row (
                modifier = Modifier
                    .background(if (isHovered) GuiDefinitions.COMPONENT_HOVER_BACKGROUND_COLOR else GuiDefinitions.COMPONENT_BACKGROUND_COLOR, shape = MaterialTheme.shapes.medium)
                    .hoverable(interactionSource = interactionSource)
            ) {
                LowLevelComposable.componentLogo("component_logos/wsl_96_dpi.png")
                // Row container for the component information
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    // Column container for the component information
                    Column (modifier = Modifier.padding(vertical = 12.dp)) {
                        ComponentActions(
                            aName = name,
                            aController = aController,
                            anUpdateAvailableState = updateAvailable,
                            aJobIsRunningState = jobIsRunning
                        )
                        ComponentInformation(
                            aVersion = version,
                            anUpdateAvailableState = updateAvailable,
                            aJobIsRunningState = jobIsRunning
                        )
                        DialogComposable.ConfirmUninstallDialog(aController)
                    }
                }
            }
        }
    }

    /**
     * Describes the component name, "Install/Update" and "More" button
     *
     */
    @Composable
    fun ComponentActions(aName: String,
                         aController: MainWindowController,
                         anUpdateAvailableState: MutableState<Boolean>,
                         aJobIsRunningState: MutableState<Boolean>) {
        //<editor-fold desc="State variables">
        // State variable for the expand section of the more options functionality
        val moreOptionsExpanded = remember { mutableStateOf(false) }
        // State variable for the coroutine scope of the possible actions
        val coroutineScope = rememberCoroutineScope()
        //</editor-fold>
        Row {
            // Text for the component name
            LowLevelComposable.componentNameText(aName)
            Spacer(modifier = Modifier.weight(1f))  // Spacer for pushing the buttons to the right
            // Install/Update button
            if (anUpdateAvailableState.value) {
                LowLevelComposable.standardButton(
                    onClickFunction = {
                        coroutineScope.launch {
                            aController.installComponent(aName, aJobIsRunningState)
                        }
                    },
                    aText = "Update",
                    isEnabled = !aJobIsRunningState.value
                )
            } else {
                LowLevelComposable.standardButton(
                    onClickFunction = {
                        coroutineScope.launch {
                            aController.installComponent(aName, aJobIsRunningState)
                        }
                    },
                    aText = "Install",
                    isEnabled = !aJobIsRunningState.value
                )
            }
            // "More" button for more options
            Box {
                IconButton(
                    onClick = {
                        moreOptionsExpanded.value = !moreOptionsExpanded.value
                    },
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
                // Dropdown menu for displaying more options
                DropdownMenu(
                    expanded = moreOptionsExpanded.value,
                    onDismissRequest = { moreOptionsExpanded.value = false }
                ) {
                    DropdownMenuItem(onClick = { /* Handle menu item click */ }) {
                        Text("Install Location")
                    }
                    DropdownMenuItem(onClick = { aController.states.confirmUninstallDialogVisible = true }) {
                        Text(
                            text = "Uninstall",
                            color = GuiDefinitions.COMPONENT_UNINSTALL_COLOR
                        )
                    }
                    // Add more DropdownMenuItems as needed
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
    fun ComponentInformation(aVersion: String? = null,
                             anUpdateAvailableState: MutableState<Boolean>,
                             aJobIsRunningState: MutableState<Boolean>) {
        Column {
            if (aVersion != null) {
                // Shows installed version
                Text(
                    text = aVersion,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 6.dp)
                )
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
                    text = "1.10.2",
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
}