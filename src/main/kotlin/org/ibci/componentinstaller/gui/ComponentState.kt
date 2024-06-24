package org.ibci.componentinstaller.gui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Job

/**
 * Class for storing component states
 *
 * @param anIsInstalledFlag Flag for installation state
 * @param anIsUpdatableFlag Flag for updatable state
 * @param anIsComponentJobRunningFlag Flag for is a component job running
 */
class ComponentState (
    anIsInstalledFlag: Boolean,
    anIsUpdatableFlag: Boolean,
    anIsComponentJobRunningFlag: Boolean = false
) {
    /**
     * State for installation
     */
    var isInstalled: MutableState<Boolean> = mutableStateOf(anIsInstalledFlag)
    /**
     * State for updatability
     */
    val isUpdatable: MutableState<Boolean> = mutableStateOf(anIsUpdatableFlag)
    /**
     * State for component job
     */
    var componentJob: MutableState<Job> = mutableStateOf(Job())
    /**
     * State for if the component job is running
     */
    var isComponentJobRunning: MutableState<Boolean> = mutableStateOf(anIsComponentJobRunningFlag)
    /**
     * State for progress description
     */
    val progressDescription = mutableStateOf("Nothing to do.")
    /**
     * Stores the dialog states related to a single component
     */
    val dialogsState = DialogsState()
}
