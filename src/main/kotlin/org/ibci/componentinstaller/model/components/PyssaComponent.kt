package org.ibci.componentinstaller.model.components

import org.ibci.componentinstaller.model.util.PathDefinitions
import org.ibci.componentinstaller.model.util.UrlDefinitions
import java.io.File

/**
 * Class for PySSA component
 *
 */
class PyssaComponent: IComponent {
    /**
     * Install a component
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    override fun install(): Boolean {
        downloadWindowsPackage()
        return true
    }

    /**
     * Downloads the Windows package for PySSA.
     *
     * @return True if operation is successful, otherwise: false
     */
    fun downloadWindowsPackage(): Boolean {
        // Create program dir under ProgramData
        if (!File(PathDefinitions.PYSSA_PROGRAM_DIR).exists()) {
            File(PathDefinitions.PYSSA_PROGRAM_DIR).mkdirs()
        }

        TODO("Continue... translate C# Io in Kotlin!")
//        // Download windows pyssa package
//        if (!Io.downloadFile(UrlDefinitions.PYSSA_WINDOWS_PACKAGE_URL, "$PathDefinitions.PYSSA_PROGRAM_DIR/windows_package.zip")) {
//            // Download failed therefore return with exit code 1
//            // logger.error("The windows_package.zip could not be downloaded!")
//            return false
//        }

        return true
    }


    /**
     * Uninstall a component
     *
     * @return True if component is successfully uninstalled, false: Otherwise
     */
    override fun uninstall(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Update a component
     *
     * @return True if component is successfully updated, false: Otherwise
     */
    override fun update(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if a component is installed
     *
     * @return True if component is installed, false: Otherwise
     */
    override fun isInstalled(): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if a component has an update
     *
     * @return True if component has update, false: Otherwise
     */
    override fun hasUpdate(): Boolean {
        TODO("Not yet implemented")
    }
}