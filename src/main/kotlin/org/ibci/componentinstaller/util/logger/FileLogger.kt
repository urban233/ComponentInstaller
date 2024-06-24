package org.ibci.componentinstaller.util.logger

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.*
import java.util.logging.Formatter

/**
 * Logger that logs message to the default log file
 *
 * TODO: This needs a better implementation. Logs are written in a strange way!
 */
class FileLogger {
    /**
     * Logger from the java logging library
     */
    private val logger: Logger = Logger.getLogger(FileLogger::class.java.name)

    /**
     * Initializer block for the class
     *
     */
    init {
        configureLogger()
    }

    /**
     * Configures the default parameter for the logger
     *
     * Adds a file handler based on a custom formatter class.
     */
    private fun configureLogger() {
        try {
            // Create a new log file for each application start
            val logFileName = "C:\\ProgramData\\IBCI\\PySSA-Installer\\logs\\${SimpleDateFormat("yyyy-MM-dd HH_mm_ss_SSS").format(Date())}.log"
            val logFilePath = Paths.get(logFileName)
            Files.createFile(logFilePath)
            // Create a file handler that appends to the log file
            val fileHandler = FileHandler(logFileName, true)
            fileHandler.formatter = CustomFormatter()
            // Add the file handler to the logger
            logger.addHandler(fileHandler)
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Failed to create log file", e)
        }
    }

    /**
     * Appends a new log message to the log file
     *
     * @param aLevel Log level to use for the message
     * @param aMessage Message to log
     *
     * This method should be used if the FileLogger is used in the application.
     * No null checks are in place because Kotlin is a null safety language and
     * a possible error couldn't be logged anyway.
     */
    fun append(aLevel: Level, aMessage: String) {
        logger.log(aLevel, formatLogMessage(aMessage))
    }

    /**
     * Formats the log message by adding the filename and line number to the message
     *
     * @param aMessage Message to add more formatting to
     *
     * No null checks are in place because Kotlin is a null safety language and
     * a possible error couldn't be logged anyway.
     */
    private fun formatLogMessage(aMessage: String): String {
        val stackTraceElement = Thread.currentThread().stackTrace[3] // Adjust index as needed
        val fileName = stackTraceElement.fileName
        val lineNumber = stackTraceElement.lineNumber
        return "[$fileName:$lineNumber] $aMessage"
    }
}

/**
 * Custom log formatter
 *
 */
internal class CustomFormatter : Formatter() {
    /**
     * Date in a specific format
     */
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    /**
     * Overrides the format method of the Formatter
     *
     * @param aRecord Record to format accordingly
     *
     * No null checks are in place because Kotlin is a null safety language and
     * a possible error couldn't be logged anyway.
     */
    override fun format(aRecord: LogRecord): String {
        val timestamp = dateFormat.format(Date(aRecord.millis))
        return "$timestamp ${aRecord.level}: ${aRecord.message}\n"
    }
}
