package org.ibci.componentinstaller.model.util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import java.io.File
import java.io.FileNotFoundException

object VersionHelper {
    /**
     * Creates a VersionHistory object from a local version history file location
     *
     * @param aVersionHistoryFilepath of the version history file to load
     * @return A VersionHistory object containing all versions of the file
     * @throws FileNotFoundException Thrown if the given filepath could not be found
     */
    fun createVersionHistoryFromLocalFile(aVersionHistoryFilepath: String) : VersionHistory {
        val tmpVersionHistoryFile = File(aVersionHistoryFilepath)
        if (!tmpVersionHistoryFile.exists()) {
            throw FileNotFoundException("aVersionHistoryFilepath could not be found.")
        }
        val tmpFileContent: String = tmpVersionHistoryFile.readText()
        return Json.decodeFromString(tmpFileContent)
    }

    /**
     * Creates a VersionHistory object from a remote location
     *
     * @param anUrl of the remote version_history.json file
     * @return A VersionHistory object containing all versions of the file
     */
    fun createVersionHistoryFromRemoteSource(anUrl: String) : VersionHistory {
        val tmpVersionHistoryFilepath: String = "${PathDefinitions.PYSSA_INSTALLER_PROGRAM_DIR}\\tmp_version_history_remote.json"
        Io.downloadFile(anUrl, tmpVersionHistoryFilepath)
        val tmpVersionHistoryFile = File(tmpVersionHistoryFilepath)
        val tmpFileContent: String = tmpVersionHistoryFile.readText()
        tmpVersionHistoryFile.delete()
        return Json.decodeFromString(tmpFileContent)
    }

    /**
     * Compare two versions
     *
     * @return 1 if the first version is newer, -1 if the second version is newer and 0 if both are equal
     */
    fun compare(aVersion: VersionInfo, aVersionToCompareAgainst: VersionInfo) : Int {
        val tmpVersion: KotlinVersion = aVersion.getAsKotlinVersion()
        val tmpVersionToCompareAgainst: KotlinVersion = aVersionToCompareAgainst.getAsKotlinVersion()
        if (tmpVersion > tmpVersionToCompareAgainst) {
            return 1
        } else if (tmpVersion < tmpVersionToCompareAgainst) {
            return -1
        } else {
            return 0
        }
    }
}
