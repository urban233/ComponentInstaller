package org.ibci.componentinstaller.gui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import org.ibci.componentinstaller.gui.DialogType
import org.ibci.componentinstaller.gui.DialogsState
import org.ibci.componentinstaller.gui.GuiDefinitions
import org.ibci.componentinstaller.main.MainWindowController
import org.ibci.componentinstaller.model.components.IComponent
import org.ibci.componentinstaller.model.util.CustomProcessBuilder
import org.ibci.componentinstaller.model.util.definitions.ComponentDefinitions
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel

/**
 * Object for storing high-level composable functions
 */
object ComposableCollection {
    @Composable
    fun componentItem(
        aComponent: IComponent,
        aController: MainWindowController,
        anIsJobRunningState: MutableState<Boolean>,
        systemState: State<List<Boolean>>
    ) {
        val tmpFileLogger = FileLogger()
        //tmpFileLogger.append(LogLevel.DEBUG, "Running componentItem for ${aComponent.name}")
        //<editor-fold desc="State variables">
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()
        val moreOptionsExpanded = remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        //</editor-fold>
        Row(
            modifier = Modifier
                .background(if (isHovered) GuiDefinitions.COMPONENT_HOVER_BACKGROUND_COLOR else GuiDefinitions.COMPONENT_BACKGROUND_COLOR, shape = MaterialTheme.shapes.medium)
                .hoverable(interactionSource = interactionSource)
        ) {
            LowLevelComposable.componentLogo(aComponent.componentInfo.componentLogoResourceFilepath)
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Row {
                        LowLevelComposable.componentNameText(aComponent.name)
                        Spacer(modifier = Modifier.weight(1f)) // Spacer to push button to the right
                        componentActions(
                            aComponent,
                            aController,
                            moreOptionsExpanded,
                            anIsJobRunningState,
                            coroutineScope,
                            aComponent.states.progressDescription,
                            systemState
                        )
                    }
                    Column {
                        componentInformation(
                            aComponent,
                            aComponent.states.progressDescription
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun componentActions(
        aComponent: IComponent,
        aController: MainWindowController,
        moreOptionsExpanded: MutableState<Boolean>,
        anIsJobRunningState: MutableState<Boolean>,
        aCoroutineScopeState: CoroutineScope,
        progressDescription: MutableState<String>,
        systemState: State<List<Boolean>>
    ) {
        val tmpFileLogger = FileLogger()
        //tmpFileLogger.append(LogLevel.DEBUG, "Runs component()")
        if (aComponent.states.isInstalled.value) {
            // Component is installed
            if (aComponent.states.isUpdatable.value) {
                // Component has an update available
                LowLevelComposable.standardButton(
                    onClickFunction = {
                        aComponent.states.componentJob.value.cancel()
                        aComponent.states.componentJob.value = aCoroutineScopeState.launch {
                            anIsJobRunningState.value = true
                            val tmpResult: Boolean = withContext(context = Dispatchers.IO) {
                                aController.updateComponent(aComponent = aComponent, aSystemState = systemState) { tmpProgressDescription ->
                                    withContext(context = Dispatchers.Main) {
                                        progressDescription.value = tmpProgressDescription
                                    }
                                }
                            }
                            anIsJobRunningState.value = false
                            if (tmpResult) {
                                aComponent.states.dialogsState.updateSuccessful.value = true
                            } else {
                                aComponent.states.dialogsState.updateFailed.value = true
                            }
                        }
                    },
                    aText = "Update",
                    isEnabled = !anIsJobRunningState.value
                )
            }
        } else {
            // Component is not installed
            LowLevelComposable.outlinedStandardButton(
                onClickFunction = {
                    aComponent.states.componentJob.value.cancel()
                    aComponent.states.componentJob.value = aCoroutineScopeState.launch {
                        anIsJobRunningState.value = true
                        val tmpResult: Boolean = withContext(context = Dispatchers.IO) {
                             aController.installComponent(aComponent) { tmpProgressDescription ->
                                withContext(context = Dispatchers.Main) {
                                    progressDescription.value = tmpProgressDescription
                                }
                            }
                        }
                        anIsJobRunningState.value = false
                        if (tmpResult) {
                            aComponent.states.dialogsState.installSuccessful.value = true
                            tmpFileLogger.append(LogLevel.INFO, "Installation finished successfully.")
                        } else {
                            aComponent.states.dialogsState.installFailed.value = true
                            tmpFileLogger.append(LogLevel.INFO, "Installation failed!")
                        }
                    }
                },
                aText = "Install",
                isEnabled = !anIsJobRunningState.value && aComponent.checkPrerequisitesForInstallation(systemState)
            )
        }
        //<editor-fold desc="Install successful dialog">
        if (aComponent.states.dialogsState.installSuccessful.value == true) {
            val tmpMessage: String
            if (aComponent.name == ComponentDefinitions.COMPONENT_NAME_WSL2) {
                tmpMessage = "Installation of ${aComponent.name} was successful. Please restart your computer and then install the next component."
            } else {
                tmpMessage = "Installation of ${aComponent.name} was successful."
            }
            DialogComposable.CustomDialog(
                onCloseRequest = { aComponent.states.dialogsState.installSuccessful.value = false },
                title = "Installation",
                content = tmpMessage
            )
        }
        //</editor-fold>
        //<editor-fold desc="Install failed dialog">
        if (aComponent.states.dialogsState.installFailed.value == true) {
            DialogComposable.CustomDialog(
                onCloseRequest = { aComponent.states.dialogsState.installFailed.value = false },
                title = "Installation",
                content = "Installation of ${aComponent.name} failed! Please try again."
            )
        }
        //</editor-fold>
        //<editor-fold desc="Uninstall successful dialog">
        if (aComponent.states.dialogsState.uninstallSuccessful.value == true) {
            val tmpMessage: String
            if (aComponent.name == ComponentDefinitions.COMPONENT_NAME_WSL2) {
                tmpMessage = "Uninstallation of ${aComponent.name} was successful. Please restart your computer to finish the process."
            } else {
                tmpMessage = "Uninstallation of ${aComponent.name} was successful."
            }
            DialogComposable.CustomDialog(
                onCloseRequest = { aComponent.states.dialogsState.uninstallSuccessful.value = false },
                title = "Uninstallation",
                content = tmpMessage
            )
        }
        //</editor-fold>
        // <editor-fold desc="Uninstall failed dialog">
        if (aComponent.states.dialogsState.uninstallFailed.value == true) {
            DialogComposable.CustomDialog(
                onCloseRequest = { aComponent.states.dialogsState.uninstallFailed.value = false },
                title = "Uninstallation",
                content = "Uninstallation of ${aComponent.name} failed! Please try again."
            )
        }
        //</editor-fold>
        // <editor-fold desc="Update successful dialog">
        if (aComponent.states.dialogsState.updateSuccessful.value == true) {
            DialogComposable.CustomDialog(
                onCloseRequest = { aComponent.states.dialogsState.updateSuccessful.value = false },
                title = "Update",
                content = "Update of ${aComponent.name} was successful."
            )
        }
        //</editor-fold>
        // <editor-fold desc="Update failed dialog">
        if (aComponent.states.dialogsState.updateFailed.value == true) {
            DialogComposable.CustomDialog(
                onCloseRequest = { aComponent.states.dialogsState.updateFailed.value = false },
                title = "Update",
                content = "Update of ${aComponent.name} failed! Please try again."
            )
        }
        //</editor-fold>
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
            if (moreOptionsExpanded.value) {
                moreOptionsMenu(
                    aComponent,
                    aController,
                    moreOptionsExpanded,
                    anIsJobRunningState,
                    progressDescription,
                    systemState
                )
            }
        }
    }

    @Composable
    fun moreOptionsMenu(
        aComponent: IComponent,
        aController: MainWindowController,
        moreOptionsExpanded: MutableState<Boolean>,
        anIsJobRunningState: MutableState<Boolean>,
        progressDescription: MutableState<String>,
        aSystemState: State<List<Boolean>>
    ) {
        DropdownMenu(
            expanded = moreOptionsExpanded.value,
            onDismissRequest = { moreOptionsExpanded.value = false },
            modifier = Modifier
                .width(200.dp)
                .background(MaterialTheme.colors.surface)
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
        ) {
            if (aComponent.states.isInstalled.value && aComponent.componentInfo.installationLocation != "") {
                LowLevelComposable.standardDropdownMenuItem(
                    onClickFunction = { openWindowsExplorer(aComponent.componentInfo.installationLocation) },
                    aText = "Open Install Location"
                )
            }
            // Uninstall menu item
            if (aComponent.states.isInstalled.value && aComponent.checkPrerequisitesForUninstallation(aSystemState) && aComponent.states.isComponentJobRunning.value == false) {
                LowLevelComposable.standardDropdownMenuItem(
                    onClickFunction = {
                        aComponent.states.dialogsState.confirmUninstall.value = true
                    },
                    aText = "Uninstall",
                    aFontColor = GuiDefinitions.COMPONENT_UNINSTALL_COLOR
                )
                if (aComponent.states.dialogsState.confirmUninstall.value == true) {
                    DialogComposable.ConfirmationDialog(
                        "Uninstall Component",
                        "Are you sure you want to uninstall ${aComponent.name}?",
                        onConfirm = {
                            aController.runUninstall(aComponent, anIsJobRunningState, progressDescription, aComponent.states.dialogsState)
                            moreOptionsExpanded.value = false
                            aComponent.states.dialogsState.confirmUninstall.value = false
                        },
                        onDismiss = {
                            moreOptionsExpanded.value = false
                            aComponent.states.dialogsState.confirmUninstall.value = false
                        }
                    )
                }
            }
            LowLevelComposable.standardDropdownMenuItem(onClickFunction = { aComponent.states.dialogsState.about.value = true }, aText = "About")
            if (aComponent.states.dialogsState.about.value == true) {
                DialogComposable.CustomDialog(
                    onCloseRequest = { aComponent.states.dialogsState.about.value = false },
                    title = "About Component ${aComponent.name}",
                    content = aComponent.componentInfo.componentDescription
                )
            }

        }
    }

    @Composable
    fun componentInformation(
        aComponent: IComponent,
        progressDescription: MutableState<String>
    ) {
        // Shows a component description
        LowLevelComposable.standardText(
            aText = aComponent.componentInfo.componentDescription
        )
        if (aComponent.states.isInstalled.value) {
            // Component is installed
            LowLevelComposable.standardText(aText = aComponent.localVersion.toString())
            if (aComponent.states.isUpdatable.value) {
                // Component has an update available
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
        if (aComponent.states.isComponentJobRunning.value) {
            //<editor-fold desc="Progress indicator">
            /*
             * Describes the progress indication with text, progress bar and cancel button
             */
            Column {
                // Container for the text and cancel button
                Row (verticalAlignment = Alignment.CenterVertically) {
                    // Progress description
                    LowLevelComposable.standardText(
                        aText = progressDescription.value
                    )
                    Spacer(modifier = Modifier.weight(1f)) // Spacer to push button to the right
                    // Cancel button
                    TextButton(onClick = {
                        aComponent.states.componentJob.value.cancel()
                        aComponent.states.componentJob.value = Job()
                        aComponent.states.isComponentJobRunning.value = false
                    },
                        enabled = aComponent.states.componentJob.value.isActive == true,
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
            //</editor-fold>
        }
    }

    fun openWindowsExplorer(anInstallationLocation: String) {
        val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
        tmpCustomProcessBuilder.runCommand(
            arrayOf(
                "/C",
                "explorer.exe",
                anInstallationLocation
            )
        )
    }

    fun openFileWithDefaultApp(aFilepath: String) {
        val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
        tmpCustomProcessBuilder.openCommand(
            arrayOf(
                "/C", aFilepath
            )
        )
    }

//    /**
//     * Describes a single component item
//     *
//     */
//    @Composable
//    fun ComponentItem(
//        aComponent: IComponent,
//        aController: MainWindowController,
//        anIsJobRunningState: MutableState<Boolean>
//    ) {
//        val interactionSource = remember { MutableInteractionSource() }
//        val isHovered by interactionSource.collectIsHoveredAsState()
//        val isUpdatable: MutableState<Boolean> = remember { mutableStateOf<Boolean>(aComponent.hasUpdate()) }
//        val isInstalled: MutableState<Boolean> = remember { mutableStateOf<Boolean>(aComponent.isInstalled()) }
//        val isComponentJobRunning = remember { mutableStateOf(false) }
//        val componentJob: MutableState<Job> = remember { mutableStateOf(Job()) } // Track the current job
//        val progressDescription = remember { mutableStateOf("A job is running ...") }
//        val showConfirmUninstallDialog: MutableState<Boolean> = remember { mutableStateOf(false) }
//        val dialogStates = remember { mutableStateMapOf<DialogType, Boolean>() }
//
//        // Container that holds all GUI elements for a single component item
//        Row(
//            modifier = Modifier
//                .background(if (isHovered) GuiDefinitions.COMPONENT_HOVER_BACKGROUND_COLOR else GuiDefinitions.COMPONENT_BACKGROUND_COLOR, shape = MaterialTheme.shapes.medium)
//                .hoverable(interactionSource = interactionSource)
//        ) {
//            LowLevelComposable.componentLogo(aComponent.componentInfo.componentLogoResourceFilepath)
//            // Container that holds the actionable buttons and component information
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Spacer(modifier = Modifier.width(8.dp))
//                Column(modifier = Modifier.padding(vertical = 12.dp)) {
//                    //<editor-fold desc="Component actions block">
//                    /*
//                    * This block describes the component name, "Install/Update" and "More" button.
//                    */
//                    // State variable for the coroutine scope
//                    val coroutineScope = rememberCoroutineScope()
//                    // State variable for the expand section of the more options functionality
//                    val moreOptionsExpanded = remember { mutableStateOf(false) }
//                    Row {
//                        LowLevelComposable.componentNameText(aComponent.name)
//                        Spacer(modifier = Modifier.weight(1f)) // Spacer to push button to the right
//
//                        if (isInstalled.value) {
//                            // Component is installed
//                            if (isUpdatable.value) {
//                                // Component has an update available
//                                LowLevelComposable.standardButton(
//                                    onClickFunction = {
//                                        componentJob.value.cancel()
//                                        componentJob.value = coroutineScope.launch {
//                                            anIsJobRunningState.value = true
//                                            isComponentJobRunning.value = true
//                                            val tmpResult: Boolean = aController.updateComponent(aComponent) { tmpProgressDescription ->
//                                                    withContext(context = Dispatchers.Main) {
//                                                        progressDescription.value = tmpProgressDescription
//                                                }
//                                            }
//                                            isInstalled.value = aComponent.isInstalled()
//                                            isComponentJobRunning.value = false
//                                            anIsJobRunningState.value = false
//                                            if (tmpResult) {
//                                                dialogStates[DialogType.UPDATE_SUCCESSFUL] = true
//                                            } else {
//                                                dialogStates[DialogType.UPDATE_FAILED] = true
//                                            }
//                                        }
//                                    },
//                                    aText = "Update",
//                                    isEnabled = !anIsJobRunningState.value
//                                )
//                            }
//                        } else {
//                            // Component is not installed
//                            LowLevelComposable.outlinedStandardButton(
//                                onClickFunction = {
//                                    componentJob.value.cancel()
//                                    componentJob.value = coroutineScope.launch {
//                                        anIsJobRunningState.value = true
//                                        isComponentJobRunning.value = true
//                                        val tmpResult: Boolean = aController.installComponent(aComponent) { tmpProgressDescription ->
//                                                withContext(context = Dispatchers.Main) {
//                                                    progressDescription.value = tmpProgressDescription
//                                            }
//                                        }
//                                        isInstalled.value = aComponent.isInstalled()
//                                        isComponentJobRunning.value = false
//                                        anIsJobRunningState.value = false
//                                        if (tmpResult) {
//                                            dialogStates[DialogType.INSTALL_SUCCESSFUL] = true
//                                        } else {
//                                            dialogStates[DialogType.INSTALL_FAILED] = true
//                                        }
//                                    }
//                                },
//                                aText = "Install",
//                                isEnabled = !anIsJobRunningState.value && aComponent.checkPrerequisitesForInstallation(systemState)
//                            )
//                        }
//                        Box {
//                            IconButton(
//                                onClick = { moreOptionsExpanded.value = !moreOptionsExpanded.value },
//                                modifier = Modifier
//                                    .width(48.dp)
//                                    .height(24.dp)
//                                    .fillMaxWidth()
//                                    .pointerHoverIcon(PointerIcon.Hand),
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.MoreVert,
//                                    contentDescription = "More Options",
//                                    modifier = Modifier
//                                        .width(40.dp)
//                                        .height(24.dp),
//                                )
//                            }
//                            DropdownMenu(
//                                expanded = moreOptionsExpanded.value,
//                                onDismissRequest = { moreOptionsExpanded.value = false },
//                                modifier = Modifier
//                                    .width(200.dp)
//                                    .background(MaterialTheme.colors.surface)
//                                    .border(1.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
//                            ) {
//                                if (isInstalled.value && !anIsJobRunningState.value && aComponent.componentInfo.installationLocation != "") {
//                                    LowLevelComposable.standardDropdownMenuItem(
//                                        onClickFunction = {
//                                            val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
//                                            tmpCustomProcessBuilder.runCommand(arrayOf("/C", "explorer.exe", aComponent.componentInfo.installationLocation))
//                                        },
//                                        aText = "Open Install Location"
//                                    )
//                                }
//                                if (isInstalled.value && aComponent.checkPrerequisitesForUninstallation()) {
//                                    LowLevelComposable.standardDropdownMenuItem(onClickFunction = {
//                                        showConfirmUninstallDialog.value = true
//                                        moreOptionsExpanded.value = false
//                                    },
//                                        aText = "Uninstall",
//                                        aFontColor = GuiDefinitions.COMPONENT_UNINSTALL_COLOR
//                                    )
//                                }
//                                LowLevelComposable.standardDropdownMenuItem(onClickFunction = {
//                                        dialogStates[DialogType.ABOUT_DIALOG] = true
//                                        moreOptionsExpanded.value = false
//                                    }, aText = "About"
//                                )
//                            }
//                        }
//                        Spacer(modifier = Modifier.width(1.dp))
//                    }
//                    //</editor-fold>
//                    //<editor-fold desc="Component information block">
//                    /*
//                     * Describes the text below the component name (including the progress indication)
//                     */
//                    Column {
//                        // Shows a component description
//                        LowLevelComposable.standardText(
//                            aText = aComponent.componentInfo.componentDescription
//                        )
//                        if (isInstalled.value) {
//                            // Component is installed
//                            LowLevelComposable.standardText(aText = aComponent.localVersion.toString())
//                            if (isUpdatable.value) {
//                                // Component has an update available
//                                Row (
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier.padding(top = 6.dp)
//                                ) {
//                                    Icon (
//                                        imageVector = Icons.Default.Info,
//                                        contentDescription = "Update info icon",
//                                        tint = GuiDefinitions.COMPONENT_INFO_ICON_COLOR,
//                                        modifier = Modifier.size(24.dp)
//                                    )
//                                    LowLevelComposable.standardText(
//                                        aText = "Update available!",
//                                        aFontSize = 13.sp,
//                                        aFontColor = Color.Black,
//                                        aFontWeight = FontWeight.SemiBold,
//                                        aModifier = Modifier
//                                            .padding(bottom = 4.dp)
//                                            .padding(horizontal = 4.dp)
//                                    )
//                                }
//                                // Shows new version number
//                                LowLevelComposable.standardText(
//                                    aText = aComponent.remoteVersion.toString()
//                                )
//                            }
//                        }
//                        if (isComponentJobRunning.value) {
//                            //<editor-fold desc="Progress indicator">
//                            /*
//                             * Describes the progress indication with text, progress bar and cancel button
//                             */
//                            Column {
//                                // Container for the text and cancel button
//                                Row (verticalAlignment = Alignment.CenterVertically) {
//                                    // Progress description
//                                    LowLevelComposable.standardText(
//                                        aText = progressDescription.value
//                                    )
//                                    Spacer(modifier = Modifier.weight(1f)) // Spacer to push button to the right
//                                    // Cancel button
//                                    TextButton(
//                                        onClick = {
//                                            componentJob.value.cancel()
//                                            anIsJobRunningState.value = false
//                                            isComponentJobRunning.value = false
//                                        },
//                                        enabled = componentJob.value.isActive == true,
//                                        modifier = Modifier
//                                            .width(100.dp)
//                                            .height(24.dp)
//                                            .fillMaxWidth()
//                                            .pointerHoverIcon(PointerIcon.Hand),
//                                        contentPadding = PaddingValues(0.dp),
//                                    ) {
//                                        LowLevelComposable.textForButton(
//                                            aText = "Cancel",
//                                            aColor = GuiDefinitions.PYSSA_BLUE_COLOR,
//                                            aFontWeight = FontWeight.SemiBold
//                                        )
//                                    }
//                                }
//                                // Progress bar
//                                LinearProgressIndicator(
//                                    color = GuiDefinitions.PYSSA_BLUE_COLOR,
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(top = 6.dp)
//                                        .clip(RoundedCornerShape(24.dp))
//                                )
//                            }
//                            //</editor-fold>
//                        }
//                    }
//                    //</editor-fold>
//                    //</editor-fold>
//                }
//            }
//        }
//    }
}
