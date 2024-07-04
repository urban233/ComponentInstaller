package org.ibci.componentinstaller.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File
import java.nio.file.Paths
import javax.swing.JOptionPane
import javax.swing.UIManager
import org.ibci.componentinstaller.model.util.Io
import org.ibci.componentinstaller.model.util.VersionHelper
import org.ibci.componentinstaller.model.util.VersionHistory
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.componentinstaller.model.util.definitions.UrlDefinitions
import org.ibci.componentinstaller.util.Utils
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
 * @param args Command line arguments
 */
fun main(args: Array<String>) {
    val tmpFileLogger = FileLogger()
    var windowTitle = "PySSA-Installer (Online Mode)"
    if (Utils.isInternetAvailable()) {
        try {
            tmpFileLogger.append(LogLevel.INFO, "Checking version of application ...")
            val localVersionHistory: VersionHistory = VersionHelper.createVersionHistoryFromLocalFile(
                PathDefinitions.PYSSA_INSTALLER_VERSION_HISTORY_JSON
            )
            val remoteVersionHistory: VersionHistory = VersionHelper.createVersionHistoryFromRemoteSource(
                UrlDefinitions.PYSSA_INSTALLER_VERSION_HISTORY
            )
            if (remoteVersionHistory.compareAgainstLatestVersionOfHistory(localVersionHistory.getLatestVersion()) == -1) {
                // An update is available!
                if (promptAndUpdate()) {
                    return
                }
            }
        } catch (ex: Exception) {
            tmpFileLogger.append(LogLevel.CRITICAL, "The exception ${ex} occurred. The program has to exit.")
            JOptionPane.showMessageDialog(null, "An error occurred. The application will now exit.", "Critical Error", JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"))
            return
        }
    } else {
        windowTitle = "PySSA-Installer (Offline Mode)"
    }
    launchGui(windowTitle)
}

/**
 * Launches the GUI installer application
 *
 * @param aWindowTitle Title of the window
 */
fun launchGui(aWindowTitle: String) = application {
    val tmpFileLogger = FileLogger()
    tmpFileLogger.append(LogLevel.INFO, "Starting GUI ...")
    tmpFileLogger.append(LogLevel.INFO, "Starting application ...")
    val tmpWindowState = rememberWindowState(width = 675.dp, height = 750.dp)
    Window(
        title = aWindowTitle,
        state = tmpWindowState,
        icon = painterResource("assets/installer_24_dpi.png"),
        onCloseRequest = ::exitApplication
    ) {
        App(MainWindowController())
    }
}

/**
 * Asks the user to update, and also inits the update process
 *
 */
fun promptAndUpdate() : Boolean {
    val response = JOptionPane.showConfirmDialog(null, "An update is available. Do you want to update now? (Please wait for further instructions.)", "Update Available", JOptionPane.YES_NO_OPTION)
    if (response == JOptionPane.YES_OPTION) {
        val tmpSetupFile: File = File("${Paths.get(System.getProperty("user.home"), "Downloads").toRealPath()}\\update_pyssa_installer_setup.exe")
        if(Io.downloadFile(UrlDefinitions.PYSSA_INSTALLER_SETUP_EXE, tmpSetupFile.absolutePath)) {
            JOptionPane.showMessageDialog(null, "Download of update finished.", "Update Downloaded", JOptionPane.INFORMATION_MESSAGE, UIManager.getIcon("OptionPane.informationIcon"))
            Runtime.getRuntime().exec(tmpSetupFile.absolutePath)
            return true
        } else {
            JOptionPane.showMessageDialog(null, "Download of update failed! Please try again later.", "Update Failed", JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"))
        }
    }
    return false
}
