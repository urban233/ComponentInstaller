package org.ibci.componentinstaller.model.util

import kotlinx.serialization.Serializable

/**
 * Class for storing a single version of the json file
 */
@Serializable
data class VersionInfo(val version: String, val releaseDate: String) {

    /**
     * Returns the version string as KotlinVersion
     *
     * @return Version as KotlinVersion object
     */
    fun getAsKotlinVersion(): KotlinVersion {
        val tmpVersionTriple = splitVersionString(version)
        return KotlinVersion(tmpVersionTriple.first, tmpVersionTriple.second, tmpVersionTriple.third)
    }

    /**
     * Splits the given version number string into an integer triple
     *
     * @return An integer triple with major, minor and patch version number
     * @throws IllegalArgumentException Thrown if the given version number has an invalid format
     * @throws NumberFormatException Thrown if major, minor or patch are null
     */
    private fun splitVersionString(versionString: String): Triple<Int, Int, Int> {
        val versionParts = versionString.split(".")
        if (versionParts.size != 3) {
            throw IllegalArgumentException("Invalid version format: Expected three parts separated by dots")
        }
        // Splitting the version parts in major, minor and patch
        val majorVersion = versionParts[0].toIntOrNull() ?: throw NumberFormatException("Invalid major version format")
        val minorVersion = versionParts[1].toIntOrNull() ?: throw NumberFormatException("Invalid minor version format")
        val patchVersion = versionParts[2].toIntOrNull() ?: throw NumberFormatException("Invalid patch version format")
        return Triple(majorVersion, minorVersion, patchVersion)
    }
}
