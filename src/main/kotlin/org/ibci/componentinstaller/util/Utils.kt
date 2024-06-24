package org.ibci.componentinstaller.util

import java.net.InetAddress

/**
 * Object container for utility functions
 */
object Utils {
    /**
     * Check if internet connection is available
     *
     * @return True if component is successfully installed, false: Otherwise
     */
    fun isInternetAvailable(): Boolean {
        try {
            val address: InetAddress = InetAddress.getByName("www.google.com")
            !address.equals("")
            return true
        } catch (e: Exception) {
            return false
        }
    }
}
