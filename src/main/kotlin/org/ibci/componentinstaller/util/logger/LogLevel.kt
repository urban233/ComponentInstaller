package org.ibci.componentinstaller.util.logger

import java.util.logging.Level

/**
 * Custom log levels
 *
 * @param aName Name of the log level
 * @param aValue An integer value for the log level
 */
class LogLevel (aName: String, aValue: Int) : Level(aName, aValue) {
    /**
     * Defines all available log levels to use with the FileLogger
     *
     * @property Debug: Use for values or other kind of debugging
     * @property Info: Use to indicate certain process begin, running or end
     * @property Warning: Use if the case is unusual and could lead to problems later on
     * @property Error: Use if an exception was caught
     * @property Critical: Use if the error handling will result in a program termination
     */
    companion object {
        val DEBUG = LogLevel("DEBUG", Level.INFO.intValue() + 1)
        val INFO = Level.INFO
        val WARNING = Level.WARNING
        val ERROR = LogLevel("ERROR", Level.SEVERE.intValue() + 1)
        val CRITICAL = LogLevel("CRITICAL", Level.SEVERE.intValue() + 2)
    }
}
