package org.ibci.componentinstaller.gui

import androidx.compose.runtime.mutableStateOf

class DialogsState {
    val about = mutableStateOf(false)
    val installSuccessful = mutableStateOf(false)
    val installFailed = mutableStateOf(false)
    val uninstallSuccessful = mutableStateOf(false)
    val uninstallFailed = mutableStateOf(false)
    val updateSuccessful = mutableStateOf(false)
    val updateFailed = mutableStateOf(false)
    val confirmUninstall = mutableStateOf(false)
}