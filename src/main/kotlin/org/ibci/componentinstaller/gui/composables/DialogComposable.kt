package org.ibci.componentinstaller.gui.composables

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
                    Button(
                        onClick = {
                            aController.states.confirmUninstallDialogVisible = false
                        }) {
                        Text("Uninstall")
                    }
                    Button(
                        onClick = {
                            aController.states.confirmUninstallDialogVisible = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}