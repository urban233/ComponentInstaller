package org.ibci.componentinstaller.model.components

/**
 * Class for storing information about a component
 *
 */
class ComponentInfo (
    aComponentLogoResourceFilepath: String,
    aComponentDescription: String
) {
    /**
     * Resource filepath to the component logo
     */
    val componentLogoResourceFilepath: String = aComponentLogoResourceFilepath
    /**
     * Short description of the component
     */
    val componentDescription: String = aComponentDescription
}
