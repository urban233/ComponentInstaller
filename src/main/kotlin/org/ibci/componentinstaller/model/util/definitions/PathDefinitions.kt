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
     * Filepath of the version history JSON file
     */
    const val PYSSA_VERSION_HISTORY_JSON: String = "${PYSSA_PROGRAM_BIN_DIR}\\PySSA\\version_history.json" // TODO: This needs to be implemented in the GitHub repo of PySSA!
    /**
     * Program path of the Installer
     */
    const val PYSSA_INSTALLER_PROGRAM_DIR: String = "C:\\ProgramData\\IBCI\\PySSA-Installer"
    /**
     * Filepath of the version history JSON file
     */
    const val PYSSA_INSTALLER_VERSION_HISTORY_JSON: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\version_history.json"
    /**
     * Filepath of the CmdElevator tool
     */
    const val CMD_ELEVATOR_EXE: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\tools\\WindowsCmdElevator.exe"
    /**
     * Filepath of the almalinux rootfs
     */
    const val PYSSA_INSTALLER_ALMALINUX_TAR: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\temp\\alma-colabfold-9-rootfs.tar"
    /**
     * Filepath of the windows package zip archive
     */
    const val PYSSA_INSTALLER_WINDOWS_PACKAGE_ZIP: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\windows_package.zip"
    /**
     * Path of the PySSA rich client
     *
     */
    const val PYSSA_RICH_CLIENT_DIR: String = "${PYSSA_PROGRAM_BIN_DIR}\\PySSA"
    /**
     * Path of Local ColabFold dir
     *
     */
    const val LOCAL_COLABFOLD_DIR: String = "C:\\ProgramData\\localcolabfold"
    /**
     * Filepath of the storage file for the almalinux distro
     */
    const val LOCAL_COLABFOLD_STORAGE_VDHX: String = "${LOCAL_COLABFOLD_DIR}\\storage\\ext4.vhdx"
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
    /**
     * Filepath of the windows wrapper executable file
     *
     */
    const val WINDOWS_TASKS_EXE: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\tools\\WindowsTasks.exe"
    /**
     * Filepath of the exchange json file
     *
     */
    const val EXCHANGE_JSON: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\tools\\exchange.json"
}
