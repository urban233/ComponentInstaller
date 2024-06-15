package org.ibci.componentinstaller.model.util.definitions

/**
 * Path definitions
 *
 */
object PathDefinitions {
    /**
     * Program folder of PySSA
     *
     */
    const val PYSSA_PROGRAM_DIR: String = "C:\\ProgramData\\IBCI\\PySSA"
    /**
     * Program path of the Installer
     *
     */
    const val PYSSA_INSTALLER_PROGRAM_DIR: String = "C:\\ProgramData\\PySSA-Installer"
    /**
     * Filepath of the offline win package zip archive
     *
     */
    const val INSTALLER_OFFLINE_WIN_PACKAGE_ZIP_FILEPATH: String = "${PYSSA_INSTALLER_PROGRAM_DIR}\\offline_win_package.zip"
    /**
     * Filepath of the windows arrangement winbatch executable
     *
     */
    const val PYSSA_WINDOW_ARRANGEMENT_EXE_FILEPATH: String = "${PYSSA_PROGRAM_DIR}\\win_start\\vb_script\\window_arrangement.exe"
    /**
     * Filepath of the PySSA windows icon
     *
     */
    const val PYSSA_ICON_FILEPATH: String = "${PYSSA_PROGRAM_DIR}\\win_start\\images\\logo.ico"
}
