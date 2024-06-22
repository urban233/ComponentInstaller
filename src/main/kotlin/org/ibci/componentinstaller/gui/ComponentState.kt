package org.ibci.componentinstaller.gui

import kotlinx.coroutines.Job

data class ComponentState (
    var isInstalled: Boolean,
    val isUpdatable: Boolean,
    var componentJob: Job,
    var isComponentJobRunning: Boolean,
)