package org.ibci.componentinstaller.gui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ibci.componentinstaller.gui.GuiDefinitions

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
            Text(
                text = aText,
                color = Color.White
            )
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
            colors = ButtonDefaults.outlinedButtonColors(contentColor = GuiDefinitions.PYSSA_BLUE_COLOR),
            border = BorderStroke(1.dp, GuiDefinitions.PYSSA_BLUE_COLOR),
            enabled = isEnabled
        ) {
            Text(text = aText)
        }
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
            fontWeight = FontWeight.SemiBold,
        )
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