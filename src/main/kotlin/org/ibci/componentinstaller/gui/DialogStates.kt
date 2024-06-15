package org.ibci.componentinstaller.gui

import androidx.compose.runtime.*

class DialogStates {
    /**
     * State variable for controlling the is "Confirm uninstall dialog" visible boolean
     */
    private val _confirmUninstallDialogVisible by lazy { mutableStateOf(false) }
    var confirmUninstallDialogVisible: Boolean
        get() = _confirmUninstallDialogVisible.value
        set(aValue) {
            _confirmUninstallDialogVisible.value = aValue
        }
    /**
     * State variable for controlling the is "About dialog" visible boolean
     */
    private val _aboutDialogVisible by lazy { mutableStateOf(false) }
    var aboutDialogVisible: Boolean
        get() = _aboutDialogVisible.value
        set(aValue) {
            _aboutDialogVisible.value = aValue
        }
}
