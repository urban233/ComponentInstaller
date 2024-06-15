package org.ibci.componentinstaller.gui

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.*

class MainWindowStates {
    /**
     * State variable for controlling the is "Installed" section expanded boolean
     *
     */
    private val _installedExpanded by lazy { mutableStateOf(true) }
    var installedExpanded: Boolean
        get() = _installedExpanded.value
        set(aValue) {
            _installedExpanded.value = aValue
        }
    /**
     * State variable for controlling the is "Available" section expanded boolean
     *
     */
    private val _availableExpanded by lazy { mutableStateOf(true) }
    var availableExpanded: Boolean
        get() = _availableExpanded.value
        set(aValue) {
            _availableExpanded.value = aValue
        }
    /**
     * State variable for controlling the is "More Options" dropdown menu expanded boolean
     *
     */
    private val _moreOptionsExpanded by lazy { mutableStateOf(false) }
    var moreOptionsExpanded: Boolean
        get() = _moreOptionsExpanded.value
        set(aValue) {
            _moreOptionsExpanded.value = aValue
        }
    /**
     * State variable for controlling the is "Update available" boolean
     *
     */
    private val _updateAvailable by lazy { mutableStateOf(false) }
    var updateAvailable: Boolean
        get() = _updateAvailable.value
        set(aValue) {
            _updateAvailable.value = aValue
        }
    /**
     * State variable for controlling the is "Confirm uninstall dialog" visible boolean
     *
     */
    private val _confirmUninstallDialogVisible by lazy { mutableStateOf(false) }
    var confirmUninstallDialogVisible: Boolean
        get() = _confirmUninstallDialogVisible.value
        set(aValue) {
            _confirmUninstallDialogVisible.value = aValue
        }
    /**
     * State variable that holds the installed components
     *
     */
    val installedComponents by lazy { mutableStateListOf<String>("WSL2") }
    /**
     * State variable that holds the available components
     *
     */
    val availableComponents by lazy { mutableStateListOf<String>("ColabFold", "PySSA") }
}
