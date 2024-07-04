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
    /**
     * Describes a basic component item
     *
     * @param aComponent Component instance
     * @param aController Main window controller instance
     * @param anIsJobRunningState A mutable boolean state
     * @param systemState List of all system states
     */
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

    /**
     * Describes the action block of the component item
     *
     * @param aComponent Component instance
     * @param aController Main window controller instance
     * @param moreOptionsExpanded A boolean mutable state instance for managing the more options action
     * @param anIsJobRunningState A boolean mutable state instance for if a job is running
     * @param aCoroutineScopeState Coroutine scope for install and update
     * @param progressDescription A string mutable state instance for the progress description
     * @param systemState List of all system states
     */
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
                    if (aComponent.name == ComponentDefinitions.COMPONENT_NAME_WSL2) {
                        aComponent.states.dialogsState.confirmInstall.value = true
                    } else {
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
        if (aComponent.states.dialogsState.confirmInstall.value == true && aComponent.name == ComponentDefinitions.COMPONENT_NAME_WSL2) {
            DialogComposable.ConfirmationDialog(
                "Install Component WSL2",
                "Are you sure you want to install WSL2?\nIMPORTANT: The WSL2 will integrate into the Windows OS and be a system component that " +
                        "cannot be uninstalled!",
                onConfirm = {
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
                    aComponent.states.dialogsState.confirmInstall.value = false
                },
                onDismiss = {
                    moreOptionsExpanded.value = false
                    aComponent.states.dialogsState.confirmInstall.value = false
                    anIsJobRunningState.value = false
                }
            )
        }
        //<editor-fold desc="Install failed dialog">
        if (aComponent.states.dialogsState.installFailed.value == true) {
            DialogComposable.CustomDialog(
                onCloseRequest = { aComponent.states.dialogsState.installFailed.value = false },
                title = "Installation",
                content = "Installation of ${aComponent.name} failed! Please try again or restart and then try again."
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

    /**
     * Describes the more options dropdown menu
     *
     * @param aComponent Component instance
     * @param aController Main window controller instance
     * @param moreOptionsExpanded A boolean mutable state instance for managing the more options action
     * @param anIsJobRunningState A boolean mutable state instance for if a job is running
     * @param progressDescription A string mutable state instance for the progress description
     * @param aSystemState List of all system states
     */
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

    /**
     * Describes the component information block of the component item
     *
     * @param aComponent Component instance
     * @param progressDescription A string mutable state instance for the progress description
     */
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
//                    TextButton(onClick = {
//                        aComponent.states.componentJob.value.cancel()
//                        aComponent.states.componentJob.value = Job()
//                        aComponent.states.isComponentJobRunning.value = false
//                    },
//                        enabled = aComponent.states.componentJob.value.isActive == true,
//                        modifier = Modifier
//                            .width(100.dp)
//                            .height(24.dp)
//                            .fillMaxWidth()
//                            .pointerHoverIcon(PointerIcon.Hand),
//                        contentPadding = PaddingValues(0.dp),
//                    ) {
//                        LowLevelComposable.textForButton(
//                            aText = "Cancel",
//                            aColor = GuiDefinitions.PYSSA_BLUE_COLOR,
//                            aFontWeight = FontWeight.SemiBold
//                        )
//                    }
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

    /**
     * Opens the MS Windows file explorer in a given path
     *
     * @param anInstallationLocation Path that should be opened in the explorer
     */
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

    /**
     * Opens a file in the default app configured in MS Windows
     *
     * @param aFilepath Path to the file to open
     */
    fun openFileWithDefaultApp(aFilepath: String) {
        val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
        tmpCustomProcessBuilder.openCommand(
            arrayOf(
                "/C", aFilepath
            )
        )
    }
}
