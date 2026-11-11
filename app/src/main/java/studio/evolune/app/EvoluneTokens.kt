package studio.evolune.app

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** Scoped V2 design tokens. Never use competing package-level color declarations. */
internal object EvoluneTokens {
    val Ink = Color(0xFF26392D)
    val Sage = Color(0xFF64816B)
    val Forest = Color(0xFF36553E)
    val Pale = Color(0xFFE7ECE2)
    val Cream = Color(0xFFFAF8F3)
    val Paper = Color(0xFFFFFEFA)
    val Muted = Color(0xFF65746C)
    val Line = Color(0xFFE5E2D9)
    val Gold = Color(0xFFB89A63)
    val appColors = lightColorScheme(
        primary=Forest,onPrimary=Color.White,
        background=Cream,onBackground=Ink,
        surface=Paper,onSurface=Ink,
        secondary=Gold,surfaceVariant=Color(0xFFEEF1E9)
    )
}
