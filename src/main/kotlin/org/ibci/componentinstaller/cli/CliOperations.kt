package org.ibci.componentinstaller.cli

import org.ibci.componentinstaller.model.components.ColabFoldComponent
import org.ibci.componentinstaller.model.components.IComponent
import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel

/**
 * Container for functions to run from the command line
 */
object CliOperations {

    /**
     * Installs a component with the --install option
     *
     */
    fun installComponent(aComponent: IComponent, theArgs: Array<String>, aFileLogger: FileLogger) {
        if (theArgs[0] == "--install" && theArgs[1] == aComponent.name) {
            aFileLogger.append(LogLevel.INFO, "User requested to install ${aComponent.name}.")
            if (!aComponent.isInstalled()) {
                aFileLogger.append(LogLevel.INFO, "Starting ${aComponent.name} installation ...")
                aComponent.install()
            } else {
                aFileLogger.append(LogLevel.INFO, "Nothing to do. ${aComponent.name} is already installed.")
            }
        }
    }

    /**
     * Uninstalls a component with the --uninstall option
     *
     */
    fun uninstallComponent(aComponent: IComponent, theArgs: Array<String>, aFileLogger: FileLogger) {
        if (theArgs[0] == "--uninstall" && theArgs[1] == aComponent.name) {
            aFileLogger.append(LogLevel.INFO, "User requested to uninstall ${aComponent.name}.")
            if (aComponent.isInstalled()) {
                aFileLogger.append(LogLevel.INFO, "Starting ${aComponent.name} uninstallation ...")
                aComponent.uninstall()
            } else {
                aFileLogger.append(LogLevel.INFO, "Nothing to do. ${aComponent.name} is already uninstalled.")
            }
        }
    }
}
