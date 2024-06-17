package org.ibci.componentinstaller.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

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

