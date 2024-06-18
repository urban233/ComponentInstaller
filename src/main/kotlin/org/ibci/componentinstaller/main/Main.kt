package org.ibci.componentinstaller.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.ibci.componentinstaller.gui.DialogType
import org.ibci.componentinstaller.gui.composables.DialogComposable
import org.ibci.componentinstaller.model.util.VersionHelper
import org.ibci.componentinstaller.model.util.VersionHistory
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.model.util.definitions.UrlDefinitions

import org.ibci.componentinstaller.util.logger.FileLogger
import org.ibci.componentinstaller.util.logger.LogLevel

/**
 * ComponentInstaller GUI build function
 *
 * @param aController Controller of the main window
 */
@Composable
@Preview
fun App(aController: MainWindowController) {
    MaterialTheme {
        MainWindow(aController)
    }
}

/**
 * ComponentInstaller entry point
 *
 */
fun main() = application {
    val fileLogger = FileLogger()
    try {
        fileLogger.append(LogLevel.INFO, "Checking version of application ...")
        val localVersionHistory: VersionHistory = VersionHelper.createVersionHistoryFromLocalFile(
            PathDefinitions.PYSSA_INSTALLER_VERSION_HISTORY_JSON
        )
        val remoteVersionHistory: VersionHistory = VersionHelper.createVersionHistoryFromRemoteSource(
            UrlDefinitions.PYSSA_INSTALLER_VERSION_HISTORY
        )
        if (remoteVersionHistory.compareAgainstLatestVersionOfHistory(localVersionHistory.getLatestVersion()) == -1) {
            // An update is available!
            return@application
        }
    } catch (ex: Exception) {
        fileLogger.append(LogLevel.CRITICAL, "The exception ${ex} occurred. The program has to exit.")
        return@application
    }
    if (args.isEmpty()) {
        // Launch GUI
        tmpFileLogger.append(LogLevel.INFO, "Starting GUI ...")
        launchGui()
    } else {
        // Handle CLI
        tmpFileLogger.append(LogLevel.INFO, "Starting CLI ...")
        launchCli(args)
    }
}

fun launchGui() {
    application {
        val fileLogger = FileLogger()
        fileLogger.append(LogLevel.INFO, "Starting application ...")
        val tmpWindowState = rememberWindowState(width = 650.dp, height = 750.dp)
        Window(
            title = "PySSA-Installer",
            state = tmpWindowState,
            icon = painterResource("installer_24_dpi.png"),
            onCloseRequest = ::exitApplication
        ) {
            App(MainWindowController())
        }
    }
}

fun launchCli(args: Array<String>) {
    println("Hello from the CLI!")
}
