package org.ibci.componentinstaller.model.util

import kotlinx.serialization.Serializable

/**
 * Class to store all versions of the json file
 */
@Serializable
data class VersionHistory(val versionHistory: Array<VersionInfo>) {

    /**
     * Gets the latest version from the history
     */
    fun getLatestVersion() : VersionInfo {
        return versionHistory.last()
    }

    /**
     * Compare a version against the latest in the history
     *
     * @return 1 if the given version is newer, -1 if the latest version is newer and 0 if both are equal
     */
    fun compareAgainstLatestVersionOfHistory(aVersion: VersionInfo) : Int {
        val tmpVersion: KotlinVersion = aVersion.getAsKotlinVersion()
        val tmpLatestVersion: KotlinVersion = versionHistory.last().getAsKotlinVersion()
        if (tmpVersion > tmpLatestVersion) {
            return 1
        } else if (tmpVersion < tmpLatestVersion) {
            return -1
        } else {
            return 0
        }
    }
}
