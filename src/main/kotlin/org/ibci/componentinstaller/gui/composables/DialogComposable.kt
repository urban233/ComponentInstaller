package org.ibci.componentinstaller.gui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ibci.componentinstaller.main.MainWindowController
import org.ibci.componentinstaller.model.components.IComponent

object DialogComposable {
    @Composable
    fun ConfirmUninstallDialog(
        aComponent: IComponent,
        aController: MainWindowController,
        aShowConfirmUninstallDialogState: MutableState<Boolean>,
        anIsInstalledState: MutableState<Boolean>,
        anIsJobRunningState: MutableState<Boolean>,
        anIsComponentJobRunning: MutableState<Boolean>,
        aProgressDescription: MutableState<String>,
        aComponentJob: MutableState<Job>
    ) {
        // Dialog that appears when dialogVisible is true
        val coroutineScope = rememberCoroutineScope()
        if (aShowConfirmUninstallDialogState.value) {
            AlertDialog(
                onDismissRequest = { aShowConfirmUninstallDialogState.value = false },
                title = {
                    Text("Confirm Uninstall")
                },
                text = {
                    Text("Are you sure you want to uninstall?")
                },
                confirmButton = {
                    Row (modifier = Modifier.padding(12.dp)) {
                        LowLevelComposable.standardButton(
                            onClickFunction = {
                                aShowConfirmUninstallDialogState.value = false
                                aComponentJob.value.cancel()
                                aComponentJob.value = coroutineScope.launch {
                                    anIsJobRunningState.value = true
                                    anIsComponentJobRunning.value = true
                                    aController.uninstallComponent(aComponent) { tmpProgressDescription ->
                                            withContext(context = Dispatchers.Main) {
                                                aProgressDescription.value = tmpProgressDescription
                                        }
                                    }
                                    anIsInstalledState.value = aComponent.isInstalled()
                                    anIsComponentJobRunning.value = false
                                    anIsJobRunningState.value = false
                                }
                            },
                            aText = "Uninstall",
                            isEnabled = true)
                        LowLevelComposable.outlinedStandardButton(
                            onClickFunction = {
                                aShowConfirmUninstallDialogState.value = false
                            },
                            aText = "Cancel",
                            isEnabled = true
                        )
                    }

                }
            )
        }
    }

    @Composable
    fun CustomDialog(onCloseRequest: () -> Unit, title: String, content: String) {
        AlertDialog(
            onDismissRequest = { onCloseRequest() },
            title = {
                Text(title)
            },
            text = {
                Column {
                    Text(content)
                }
            },
            confirmButton = {
                Row (modifier = Modifier.padding(12.dp)) {
                    LowLevelComposable.standardButton(
                        onClickFunction = { onCloseRequest() },
                        aText = "OK",
                        isEnabled = true
                    )
                }
            }
        )
    }
}
