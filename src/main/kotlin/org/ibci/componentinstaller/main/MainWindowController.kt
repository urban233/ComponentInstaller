package org.ibci.componentinstaller.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*
import org.ibci.componentinstaller.gui.DialogType
import org.ibci.componentinstaller.gui.DialogsState
import org.ibci.componentinstaller.model.components.IComponent
import org.ibci.componentinstaller.util.logger.FileLogger

class MainWindowController() {
    /**
     * Class file logger
     */
    private val fileLogger: FileLogger = FileLogger()

    /**
     * Installs a component
     *
     */
    suspend fun installComponent(
        aComponent: IComponent,
        aProgressDescriptionUpdate: suspend (String) -> Unit
    ) : Boolean {
        aComponent.states.isComponentJobRunning.value = true
        aProgressDescriptionUpdate("Starting installation ...")
        delay(2000)
        aProgressDescriptionUpdate("Running installation process ...")
        delay(2000)
        if (aComponent.install()) {
            aProgressDescriptionUpdate("Finish up installation ...")
            delay(2000)
        }
        aComponent.states.isComponentJobRunning.value = false
        aComponent.states.isInstalled.value = aComponent.isInstalled()
        return aComponent.states.isInstalled.value
    }

    /**
     * Uninstalls a component
     *
     */
    suspend fun uninstallComponent(
        aComponent: IComponent,
        aProgressDescriptionUpdate: suspend (String) -> Unit
    ) : Boolean {
        aComponent.states.isComponentJobRunning.value = true
        val tmpComponentName: String = aComponent.name
        aProgressDescriptionUpdate("Removing component $tmpComponentName ...")
        delay(2000)
        aProgressDescriptionUpdate("Running removing process ...")
        delay(2000)
        if (aComponent.uninstall()) {
            aProgressDescriptionUpdate("Finish up removing ...")
            delay(2000)
        }
        aComponent.states.isComponentJobRunning.value = false
        aComponent.states.isInstalled.value = aComponent.isInstalled()
        return !aComponent.states.isInstalled.value
    }

    fun runUninstall(
        aComponent: IComponent,
        anIsJobRunningState: MutableState<Boolean>,
        aProgressDescription: MutableState<String>,
        dialogsState: DialogsState
    ) {
        val coroutineScope = CoroutineScope(Dispatchers.Default)
        aComponent.states.componentJob.value.cancel()
        aComponent.states.componentJob.value = coroutineScope.launch {
            anIsJobRunningState.value = true
            val tmpResult = uninstallComponent(aComponent) { tmpProgressDescription ->
                withContext(context = Dispatchers.Main) {
                    aProgressDescription.value = tmpProgressDescription
                }
            }
            anIsJobRunningState.value = false
            if (tmpResult) {
                dialogsState.uninstallSuccessful.value = true
            } else {
                dialogsState.uninstallFailed.value = true
            }
        }
    }

    /**
     * Update a component
     *
     */
    suspend fun updateComponent(
        aComponent: IComponent,
        aSystemState: State<List<Boolean>>,
        aProgressDescriptionUpdate: suspend (String) -> Unit
    ) : Boolean {
        aComponent.states.isComponentJobRunning.value = true
        val tmpComponentName: String = aComponent.name
        aProgressDescriptionUpdate("Updating component $tmpComponentName ...")
        delay(2000)
        aProgressDescriptionUpdate("Running update process ...")
        delay(2000)
        if(aComponent.update(aSystemState)) {
            aProgressDescriptionUpdate("Finishing up update ...")
            delay(2000)
        }
        aComponent.states.isComponentJobRunning.value = false
        aComponent.states.isInstalled.value = aComponent.isInstalled()
        return aComponent.states.isInstalled.value
    }
}
