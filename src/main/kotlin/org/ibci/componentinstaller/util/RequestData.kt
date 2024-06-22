package org.ibci.componentinstaller.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import java.io.File

/**
 * Class to store the operation type and its data for a request to the Windows wrapper
 *
 */
@Serializable
data class RequestData(val type: Int, val data: Array<String>) {

    fun writeToJsonFile() : Boolean {
        try {
            val jsonString = Json.encodeToString(this)
            val file = File(PathDefinitions.EXCHANGE_JSON)
            file.writeText(jsonString)
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }
        return true
    }
}

