package org.ibci.componentinstaller.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.ibci.componentinstaller.model.util.PythonHelper
import org.ibci.componentinstaller.model.util.definitions.PathDefinitions
import org.ibci.extension.communication.*
import java.io.File
import java.net.InetAddress


fun main() {
    try {
        val address: InetAddress = InetAddress.getByName("www.google.com")
        !address.equals("")
        println("Has internet")
    } catch (e: Exception) {
        println(e.message)
    }

//    val tmpHelper = PythonHelper()
//    tmpHelper.setupVenv()


   // val tmpCommunicator: Communicator = Communicator()
    // Serialize the data class to a JSON string
//    val tmpData = RequestData(
//        OperationTypeDefinitions.CREATE_SHORTCUTS,
//        arrayOf(
//            PathDefinitions.PYSSA_WINDOW_ARRANGEMENT_EXE,
//            "PySSA",
//            PathDefinitions.PYSSA_LOGO_ICO
//        )
//    )
//    val tmpData = RequestData(
//        OperationTypeDefinitions.UNZIP_ARCHIVE,
//        arrayOf(
//            "C:\\ProgramData\\IBCI\\PySSA\\windows_package.zip",
//            "C:\\ProgramData\\IBCI\\PySSA\\"
//        )
//    )
//    val tmpData = RequestData(
//        OperationTypeDefinitions.RUN_CMD_COMMAND,
//        arrayOf("${PathDefinitions.PYSSA_PROGRAM_BIN_DIR}\\setup_python_for_pyssa\\setup_python.bat")
//    )
//    if (!tmpData.writeToJsonFile()) {
//        return
//    }
//    if (!tmpCommunicator.sendRequest(PathDefinitions.EXCHANGE_JSON, false)) {
//        println("Request failed!")
//    } else {
//        println(tmpCommunicator.lastReply)
//    }

//    val tmpCustomProcessBuilder: CustomProcessBuilder = CustomProcessBuilder()
//    val runasCommand = arrayOf("/c", "runas /user:Administrator", "echo Hello")
//    val tmpResult = tmpCustomProcessBuilder.runCommand(runasCommand)
//    for (tmpLine in tmpResult) {
//        println(tmpLine)
//    }
}