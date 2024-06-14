package org.ibci.componentinstaller.gui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            modifier = Modifier
                .background(Color(54, 122, 246)) // Black background
                .padding(16.dp) // Padding around the text
                .fillMaxWidth()
        ) {
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
    fun ComponentItem(name: String, aController: MainWindowController, version: String? = null, updateAvailable: Boolean = false) {
        if (name != "") {
            var expanded by remember { mutableStateOf(false) }
            var dialogVisible by remember { mutableStateOf(false) }
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered by interactionSource.collectIsHoveredAsState()

            // Load image using painterResource or imageResource
            val image = painterResource("component_logos/wsl_96_dpi.png")

            // Display the image using Image composable

            Row (
                modifier = Modifier
                    .background(if (isHovered) GuiDefinitions.COMPONENT_HOVER_BACKGROUND_COLOR else GuiDefinitions.COMPONENT_BACKGROUND_COLOR, shape = MaterialTheme.shapes.medium)
                    .hoverable(interactionSource = interactionSource)
            ) {
                Image(
                    painter = image,
                    contentDescription = "Logo Image",
                    modifier = Modifier
                        .size(125.dp)
                        .padding(top = 12.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Column (modifier = Modifier.padding(vertical = 12.dp)) {
                        Row {
                            Text(
                                name,
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                                //modifier = Modifier.padding(vertical = 12.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))  // Spacer for pushing the buttons to the right
                            if (updateAvailable) {
                                TextButton(onClick = { /* Handle update */ }) {
                                    Text("Update")
                                }
                            } else {
                                Button(
                                    onClick = {
                                        aController.installComponent(name)
                                    },
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(24.dp)
                                        .fillMaxWidth()
                                        .pointerHoverIcon(PointerIcon.Hand),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(25),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = GuiDefinitions.PYSSA_BLUE_COLOR)
                                ) {
                                    Text(
                                        text = "Install",
                                        color = Color.White
                                    )
                                }
                            }
                            Box {
                                IconButton(
                                    onClick = {
                                        expanded = !expanded
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
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(onClick = { dialogVisible = true }) {
                                        Text("Uninstall")
                                    }
                                    DropdownMenuItem(onClick = { /* Handle menu item click */ }) {
                                        Text("Install Location")
                                    }
                                    // Add more DropdownMenuItems as needed
                                }
                            }
                            Spacer(modifier = Modifier.width(1.dp))
                        }
                        Column {
                            if (version != null) {
                                Text(
                                    text = version,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            // Component description text
                            Text(
                                text = "This component can do cool things.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            // Update text
                            Row (verticalAlignment = Alignment.CenterVertically) {
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

                            Text(
                                text = "1.10.2",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                    // Dialog that appears when dialogVisible is true
                    if (dialogVisible) {
                        AlertDialog(
                            onDismissRequest = { dialogVisible = false },
                            title = {
                                Text("Confirm Uninstall")
                            },
                            text = {
                                Text("Are you sure you want to uninstall?")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        dialogVisible = false
                                    }
                                ) {
                                    Text("Uninstall")
                                }
                                Button(
                                    onClick = {
                                        dialogVisible = false
                                    }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
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
}