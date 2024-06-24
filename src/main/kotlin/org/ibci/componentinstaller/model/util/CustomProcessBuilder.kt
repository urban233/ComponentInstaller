package org.ibci.componentinstaller.model.util

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Class for running CLI commands
 *
 */
class CustomProcessBuilder {
    /**
     * Process builder to run the commands
     */
    private val cmdBuilder: ProcessBuilder = ProcessBuilder()

    /**
     * Runs a command and returns its standard output in the form of a string array
     *
     * @param aCommand An array containing the command and flags to run
     * @param anExecutable Executable file that should be used to run the command against (Optional, default: cmd.exe)
     *
     * @return A string array containing the command console output
     * @throws IllegalArgumentException Thrown if `aCommand` is either an empty array or the first element is an empty string
     */
    fun runCommand(aCommand: Array<String>, anExecutable: String = "cmd.exe") : Array<String> {
        if (aCommand.isEmpty() || aCommand[0] == "") {
            throw IllegalArgumentException("aCommand is either an empty array or the first element is an empty string")
        }
        try {
            this.cmdBuilder.command(anExecutable, *aCommand)
            val process: Process = cmdBuilder.start()
            process.waitFor()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            return reader.readLines().toTypedArray()
        } catch (ex: Exception) {
            ex.printStackTrace()
            return emptyArray()
        }
    }

    /**
     * Runs a command and does not wait for its completion and returns the process object
     *
     * @param aCommand An array containing the command and flags to run
     * @param anExecutable Executable file that should be used to run the command against (Optional, default: cmd.exe)
     *
     * @return A process object if the command could be opened, Otherwise: null
     * @throws IllegalArgumentException Thrown if `aCommand` is either an empty array
     */
    fun openCommand(aCommand: Array<String>, anExecutable: String = "cmd.exe") : Process? {
        if (aCommand.isEmpty()) {
            throw IllegalArgumentException("aCommand is either an empty array")
        }
        try {
            this.cmdBuilder.command(anExecutable, *aCommand)
            val process: Process = cmdBuilder.start()
            return process
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }
}