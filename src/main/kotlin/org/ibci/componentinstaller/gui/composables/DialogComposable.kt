package org.ibci.componentinstaller.gui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ibci.componentinstaller.gui.MainWindowController

object DialogComposable {
    @Composable
    fun ConfirmUninstallDialog(aController: MainWindowController) {
        // Dialog that appears when dialogVisible is true
        if (aController.states.confirmUninstallDialogVisible) {
            AlertDialog(
                onDismissRequest = { aController.states.confirmUninstallDialogVisible = false },
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
                                aController.states.confirmUninstallDialogVisible = false
                            },
                            aText = "Uninstall",
                            isEnabled = true)
                        LowLevelComposable.outlinedStandardButton(
                            onClickFunction = {
                                aController.states.confirmUninstallDialogVisible = false
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
    fun AboutDialog(aController: MainWindowController) {
        // Dialog that appears when dialogVisible is true
        if (aController.states.aboutDialogVisible) {
            AlertDialog(
                onDismissRequest = { aController.states.aboutDialogVisible = false },
                title = {
                    Text("About Component")
                },
                text = {
                    Column {
                        Text("Windows Subsystem for Linux (WSL) is a feature of Windows that allows you to run a Linux environment on your Windows machine, without the need for a separate virtual machine or dual booting. WSL is designed to provide a seamless and productive experience for developers who want to use both Windows and Linux at the same time.")
                        Text("More information under: https://learn.microsoft.com/en-us/windows/wsl/about")
                    }
                },
                confirmButton = {
                    Row (modifier = Modifier.padding(12.dp)) {
                        LowLevelComposable.standardButton(
                            onClickFunction = {
                                aController.states.aboutDialogVisible = false
                            },
                            aText = "OK",
                            isEnabled = true)
                    }
                }
            )
        }
    }
}