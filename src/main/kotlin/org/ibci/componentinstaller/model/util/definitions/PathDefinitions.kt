package org.ibci.componentinstaller.model.util.definitions

/**
 * Path definitions
 *
 */
object PathDefinitions {
    /**
     * Program folder of PySSA
     */
    const val PYSSA_PROGRAM_DIR: String = "C:\\ProgramData\\IBCI\\PySSA"
    /**
     * Program folder of PySSA bin dir
     *
     */
    const val PYSSA_PROGRAM_BIN_DIR: String = "${PYSSA_PROGRAM_DIR}\\bin"
    /**
     * Program path of the Installer
     */
    const val PYSSA_INSTALLER_PROGRAM_DIR: String = "C:\\ProgramData\\IBCI\\PySSA-Installer"
    /**
     * Filepath of the offline win package zip archive
     */
    const val PYSSA_INSTALLER_OFFLINE_WIN_PACKAGE_ZIP: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\offline_win_package.zip"
    /**
     * Path of the PySSA rich client
     *
     */
    const val PYSSA_RICH_CLIENT_DIR: String = "${PYSSA_PROGRAM_BIN_DIR}\\PySSA"
    /**
     * Filepath of the checkWsl batch script
     */
    const val CHECK_WSL_BAT: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\scripts\\checkWsl.bat"
    /**
     * Filepath of createShortcut powershell script
     */
    const val CREATE_SHORTCUTS_PS1: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\scripts\\createShortcuts.ps1"
    /**
     * Filepath of removeShortcut powershell script
     */
    const val REMOVE_SHORTCUTS_PS1: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\scripts\\removeShortcuts.ps1"
    /**
     * Filepath of the .wsl_installed file (indicating that the wsl is installed)
     */
    const val WSL_INSTALLED: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\.wsl_installed"
    /**
     * Filepath of the windows arrangement winbatch executable
     */
    const val PYSSA_WINDOW_ARRANGEMENT_EXE: String = "${PYSSA_PROGRAM_DIR}\\win_start\\vb_script\\window_arrangement.exe"
    /**
     * Filepath of the PySSA windows icon
     */
    const val PYSSA_LOGO_ICO: String = "${PYSSA_PROGRAM_DIR}\\win_start\\images\\logo.ico"
    /**
     * Filepath of the pymol.exe file
     *
     */
    const val PYMOL_EXE: String = "${PYSSA_PROGRAM_BIN_DIR}\\.venv\\Scripts\\pymol.exe"
    /**
     * Filepath of the pip executable file
     *
     */
    const val PIP_EXE: String = "${PYSSA_PROGRAM_BIN_DIR}\\.venv\\Scripts\\pip.exe"
}
