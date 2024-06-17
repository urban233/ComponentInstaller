package org.ibci.componentinstaller.gui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import org.ibci.componentinstaller.gui.GuiDefinitions
import org.ibci.componentinstaller.util.CustomFonts

object LowLevelComposable {

    /**
     * Describes a button used for installing any component in
     */
    @Composable
    fun standardButton(
        onClickFunction: () -> Unit,
        aText: String,
        isEnabled: Boolean,
    ) {
        Button(
            onClick = { onClickFunction() },
            modifier = Modifier
                .width(100.dp)
                .height(24.dp)
                .fillMaxWidth()
                .pointerHoverIcon(PointerIcon.Hand),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(25),
            colors = ButtonDefaults.buttonColors(backgroundColor = GuiDefinitions.PYSSA_BLUE_COLOR),
            enabled = isEnabled
        ) {
            textForButton(aText = aText, aColor = Color.White)
        }
    }

    /**
     * Describes an alternative button design used for less actionable tasks
     */
    @Composable
    fun outlinedStandardButton(
        onClickFunction: () -> Unit,
        aText: String,
        isEnabled: Boolean,
    ) {
        OutlinedButton(
            onClick = { onClickFunction() },
            modifier = Modifier
                .width(100.dp)
                .height(24.dp)
                .fillMaxWidth()
                .pointerHoverIcon(PointerIcon.Hand),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(25),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                backgroundColor = Color.White,
                disabledContentColor = Color.LightGray,
                disabledBackgroundColor = GuiDefinitions.DISABLED_BUTTON_BACKGROUND_COLOR),
            border = BorderStroke(1.dp, Color.LightGray),
            enabled = isEnabled
        ) {
            if (isEnabled) {
                textForButton(aText = aText, aColor = Color.Black)
            } else {
                textForButton(aText = aText, aColor = Color.LightGray)
            }
        }
    }

    /**
     * Describes a standard text composable
     *
     */
    @Composable
    fun standardText(
        aText: String,
        aFontSize: TextUnit = 12.sp,
        aFontColor: Color = Color.Gray,
        aFontWeight: FontWeight = FontWeight.Normal,
        aModifier: Modifier = Modifier.padding(top = 6.dp)
    ) {
        Text(
            text = aText,
            fontSize = aFontSize,
            color = aFontColor,
            fontWeight = aFontWeight,
            fontFamily = GuiDefinitions.DEFAULT_FONT_FAMILY,
            modifier = aModifier
        )
    }

    /**
     * Describes the text displayed on a button
     *
     */
    @Composable
    fun textForButton(aText: String, aColor: Color, aFontWeight: FontWeight = FontWeight.Medium) {
        Text(
            text = aText,
            color = aColor,
            fontWeight = aFontWeight,
            fontFamily = GuiDefinitions.DEFAULT_FONT_FAMILY
        )
    }

    /**
     * Describes the component name text
     *
     */
    @Composable
    fun componentNameText(aName: String) {
        Text(
            text = aName,
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontFamily = GuiDefinitions.DEFAULT_FONT_FAMILY
        )
    }

    /**
     * Describes a standard dropdown menu item
     *
     */
    @Composable
    fun standardDropdownMenuItem(
        onClickFunction: () -> Unit,
        aText: String,
        aFontColor: Color = Color.Black,
    ) {
        DropdownMenuItem(
            onClick = { onClickFunction() },
            modifier = Modifier
                .pointerHoverIcon(PointerIcon.Hand)
        ) {
            Text(
                text = aText,
                color = aFontColor,
                fontWeight = FontWeight.Normal,
                fontFamily = GuiDefinitions.DEFAULT_FONT_FAMILY,
                fontSize = 15.sp
            )
//            Text(
//                text = aText,
//                color = aFontColor,
//                fontSize = 13.sp
//            )
        }
    }

    /**
     * Describes the component logo
     *
     */
    @Composable
    fun componentLogo(aLogoResourcePath: String) {
        Image(
            painter = painterResource(aLogoResourcePath),
            contentDescription = "Logo Image",
            modifier = Modifier
                .size(125.dp)
                .padding(top = 12.dp)
        )
    }
}