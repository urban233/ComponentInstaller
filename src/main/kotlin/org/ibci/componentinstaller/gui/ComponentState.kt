package org.ibci.componentinstaller.gui

import kotlinx.coroutines.Job

data class ComponentState (
    val isInstalled: Boolean,
    val isUpdatable: Boolean,
    val componentJob: Job,
    val isComponentJobRunning: Boolean,
)