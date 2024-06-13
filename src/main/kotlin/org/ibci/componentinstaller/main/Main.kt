package org.ibci.componentinstaller.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

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
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
