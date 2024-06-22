package org.ibci.componentinstaller.gui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ibci.componentinstaller.gui.DialogType
import org.ibci.componentinstaller.main.MainWindowController
import org.ibci.componentinstaller.model.components.IComponent

object DialogComposable {
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
