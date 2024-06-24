package org.ibci.componentinstaller.gui

import androidx.compose.runtime.mutableStateOf

/**
 * Class for storing states for different dialogs of a single component
 */
class DialogsState {
    /**
     * State for about dialog
     */
    val about = mutableStateOf(false)
    /**
     * State for installSuccessful dialog
     */
    val installSuccessful = mutableStateOf(false)
    /**
     * State for installFailed dialog
     */
    val installFailed = mutableStateOf(false)
    /**
     * State for uninstallSuccessful dialog
     */
    val uninstallSuccessful = mutableStateOf(false)
    /**
     * State for uninstallFailed dialog
     */
    val uninstallFailed = mutableStateOf(false)
    /**
     * State for updateSuccessful dialog
     */
    val updateSuccessful = mutableStateOf(false)
    /**
     * State for updateFailed dialog
     */
    val updateFailed = mutableStateOf(false)
    /**
     * State for confirmUninstall dialog
     */
    val confirmUninstall = mutableStateOf(false)
    /**
     * State for confirmInstall dialog
     */
    val confirmInstall = mutableStateOf(false)
}