package org.ibci.componentinstaller.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

import org.ibci.componentinstaller.gui.*

/**
 * ComponentInstaller GUI build function
 *
 */
@Composable
@Preview
fun App() {
    val tmpMainWindow = MainWindow()
    MaterialTheme {
        tmpMainWindow.MainFrame()
    }
}

/**
 * ComponentInstaller entry point
 *
 */
fun main() = application {
    val tmpWindowState = rememberWindowState(width = 650.dp, height = 750.dp)
    Window(
        title = "PySSA-Installer",
        state = tmpWindowState,
        icon = painterResource("pyssa_installer_logo.png"),
        onCloseRequest = ::exitApplication) {
        App()
    }
}
