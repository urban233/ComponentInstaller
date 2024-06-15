package org.ibci.componentinstaller.gui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
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
}