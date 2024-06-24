package org.ibci.componentinstaller.util

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

/**
 * Object container for custom fonts
 */
object CustomFonts {
    /**
     * Custom font family Roboto
     *
     * Downloaded font files from https://fonts.google.com/specimen/Roboto?query=Noto&classification=Display&stroke=Sans+Serif&subset=latin&noto.script=Latn
     */
    val Roboto = FontFamily(
        Font("assets/fonts/Roboto/Roboto-Black.ttf", FontWeight.Black),
        Font("assets/fonts/Roboto/Roboto-Bold.ttf", FontWeight.Bold),
        Font("assets/fonts/Roboto/Roboto-Light.ttf", FontWeight.Light),
        Font("assets/fonts/Roboto/Roboto-Medium.ttf", FontWeight.Medium),
        Font("assets/fonts/Roboto/Roboto-Regular.ttf", FontWeight.Normal),
        Font("assets/fonts/Roboto/Roboto-Thin.ttf", FontWeight.ExtraLight),
    )
    /**
     * Custom font family Roboto
     *
     * Downloaded font files from https://fonts.google.com/noto/specimen/Noto+Sans+Display?query=Noto&classification=Display&stroke=Sans+Serif&subset=latin&noto.script=Latn
     */
    val NotoSansDisplay = FontFamily(
        Font("assets/fonts/NotoSansDisplay/NotoSansDisplay-Black.ttf", FontWeight.Black),
        Font("assets/fonts/NotoSansDisplay/NotoSansDisplay-Bold.ttf", FontWeight.Bold),
        Font("assets/fonts/NotoSansDisplay/NotoSansDisplay-ExtraLight.ttf", FontWeight.ExtraLight),
        Font("assets/fonts/NotoSansDisplay/NotoSansDisplay-Light.ttf", FontWeight.Light),
        Font("assets/fonts/NotoSansDisplay/NotoSansDisplay-Medium.ttf", FontWeight.Medium),
        Font("assets/fonts/NotoSansDisplay/NotoSansDisplay-Regular.ttf", FontWeight.Normal),
        Font("assets/fonts/NotoSansDisplay/NotoSansDisplay-SemiBold.ttf", FontWeight.SemiBold),
    )
    /**
     * Custom font family Inter Tight
     *
     * Downloaded font files from https://fonts.google.com/specimen/Inter+Tight?query=inter&classification=Display&stroke=Sans+Serif&subset=latin&noto.script=Latn
     */
    val InterTight = FontFamily(
        Font("assets/fonts/InterTight/InterTight-Black.ttf", FontWeight.Black),
        Font("assets/fonts/InterTight/InterTight-Bold.ttf", FontWeight.Bold),
        Font("assets/fonts/InterTight/InterTight-ExtraBold.ttf", FontWeight.ExtraBold),
        Font("assets/fonts/InterTight/InterTight-ExtraLight.ttf", FontWeight.ExtraLight),
        Font("assets/fonts/InterTight/InterTight-Light.ttf", FontWeight.Light),
        Font("assets/fonts/InterTight/InterTight-Medium.ttf", FontWeight.Medium),
        Font("assets/fonts/InterTight/InterTight-Regular.ttf", FontWeight.Normal),
        Font("assets/fonts/InterTight/InterTight-SemiBold.ttf", FontWeight.SemiBold),
    )
}