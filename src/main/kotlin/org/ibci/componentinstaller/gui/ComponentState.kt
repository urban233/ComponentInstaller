package org.ibci.componentinstaller.gui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.Job

class ComponentState (
    anIsInstalledFlag: Boolean,
    anIsUpdatableFlag: Boolean,
    anIsComponentJobRunningFlag: Boolean = false
) {
    var isInstalled: MutableState<Boolean> = mutableStateOf(anIsInstalledFlag)
    val isUpdatable: MutableState<Boolean> = mutableStateOf(anIsUpdatableFlag)
    var componentJob: MutableState<Job> = mutableStateOf(Job())
    var isComponentJobRunning: MutableState<Boolean> = mutableStateOf(anIsComponentJobRunningFlag)
    val progressDescription = mutableStateOf("Nothing to do.")
    val dialogsState = DialogsState()
}
