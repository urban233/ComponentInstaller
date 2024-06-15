package org.ibci.componentinstaller.model.util

/**
 * Class that contains static methods for IO.
 */
object Io {
    /**
     * Downloads a file in a synchronous manner.
     *
     * @param anUrl The URL to download the file from.
     * @param aFilepath The filepath to save the downloaded file.
     * @return A boolean indicating if the download was successful.
     * @throws IllegalArgumentException Gets thrown if any of the arguments are null.
     */

    fun downloadFile(anUrl: String?, aFilepath: String?): Boolean {
        //<editor-fold desc="Checks">
        if (anUrl == null) {
            throw IllegalArgumentException("anUrl is null.")
        }
        if (aFilepath == null) {
            throw IllegalArgumentException("aFilepath is null.")
        }
        //</editor-fold>

        val tmpClient = java.net.URL(anUrl).openStream()
        return try {
            tmpClient.use { input ->
                java.io.FileOutputStream(aFilepath).use { output ->
                    input.copyTo(output)
                }
            }
            java.io.File(aFilepath).exists()
        } catch (e: Exception) {
            false
        }
    }
}
