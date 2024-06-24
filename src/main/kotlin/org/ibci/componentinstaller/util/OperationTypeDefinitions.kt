package org.ibci.componentinstaller.util

/**
 * Object container for operation type definitions
 *
 * These values are used in conjunctions with the WindowsTasks.
 */
object OperationTypeDefinitions {
    /**
     * Type value for UNZIP_ARCHIVE
     */
    val UNZIP_ARCHIVE = 0
    /**
     * Type value for CREATE_SHORTCUTS
     */
    val CREATE_SHORTCUTS = 1
    /**
     * Type value for REMOVE_SHORTCUTS
     */
    val REMOVE_SHORTCUTS = 2
    /**
     * Type value for RUN_CMD_COMMAND
     */
    val RUN_CMD_COMMAND = 3
    /**
     * Type value for CHECK_WSL_INSTALLATION
     */
    val CHECK_WSL_INSTALLATION = 4
    /**
     * Type value for CLOSE_CONNECTION
     */
    val CLOSE_CONNECTION = 99
}