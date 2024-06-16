package org.ibci.componentinstaller.gui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import org.ibci.componentinstaller.util.CustomFonts

object GuiDefinitions {
    /**
     * Brand color of PySSA
     */
    val PYSSA_BLUE_COLOR: Color = Color(54, 122, 246)
    /**
     * Disabled button background color
     */
    val DISABLED_BUTTON_BACKGROUND_COLOR = Color(247, 248, 250)
    /**
     * Background color for the component item
     */
    val COMPONENT_BACKGROUND_COLOR: Color = Color.White
    /**
     * Background color for the hovered component item
     */
    val COMPONENT_HOVER_BACKGROUND_COLOR: Color = Color(235, 246, 255)
    /**
     * Color for the info icon
     */
    val COMPONENT_INFO_ICON_COLOR: Color = Color(0, 96, 192)
    /**
     * Color for uninstall text
     */
    val COMPONENT_UNINSTALL_COLOR: Color = Color(179, 54, 55)
    /**
     * Default font family
     */
    val DEFAULT_FONT_FAMILY: FontFamily = CustomFonts.InterTight
}