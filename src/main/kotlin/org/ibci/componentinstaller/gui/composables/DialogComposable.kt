package org.ibci.componentinstaller.gui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Object for storing dialog composable functions
 */
object DialogComposable {
    /**
     * Describes a basic confirmation dialog
     *
     * @param title Title of the dialog
     * @param message Message to display in the dialog
     * @param onConfirm Function to run on confirmation
     * @param onDismiss Function to run on dismiss
     */
    @Composable
    fun ConfirmationDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                LowLevelComposable.standardText(
                    aText = title,
                    aFontSize = 15.sp,
                    aFontWeight = FontWeight.Bold,
                    aFontColor = Color.Black
                ) },
            text = {
                LowLevelComposable.standardText(
                    aText = message,
                    aFontSize = 13.sp
                ) },
            buttons = {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    LowLevelComposable.standardButton(onClickFunction = onConfirm, "OK", true)
                    Spacer(modifier = Modifier.weight(0.05f))
                    LowLevelComposable.outlinedStandardButton(onClickFunction = onDismiss, "Cancel", true)
                }
            }
        )
    }

    /**
     * Describes a custom dialog
     *
     * @param onCloseRequest Function to run if dialog closes
     * @param title Title of the dialog
     * @param content Content to display in the dialog
     */
    @Composable
    fun CustomDialog(onCloseRequest: () -> Unit, title: String, content: String) {
        AlertDialog(
            onDismissRequest = { onCloseRequest() },
            title = {
                LowLevelComposable.standardText(
                    aText = title,
                    aFontSize = 15.sp,
                    aFontWeight = FontWeight.Bold
                ) },
            text = {
                Column {
                    LowLevelComposable.standardText(
                        aText = content,
                        aFontSize = 13.sp
                    )
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
