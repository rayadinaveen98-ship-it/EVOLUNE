package studio.evolune.app

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * EVOLUNE V2 Stage 07: a single editorial type ladder.
 *
 * Native system fonts are used intentionally. They support font-scale accessibility
 * and broad script fallback without licensing or downloading unapproved font files.
 * Serif is reserved for display and reflection; sans-serif carries tasks and controls.
 * All sizes are sp, so Android's font-scale choice remains respected.
 */
internal object EvoluneType {
    val typography = Typography(
        displayLarge=TextStyle(
            fontFamily=FontFamily.Serif,fontSize=36.sp,lineHeight=44.sp,
            fontWeight=FontWeight.Normal
        ),
        displayMedium=TextStyle(
            fontFamily=FontFamily.Serif,fontSize=30.sp,lineHeight=38.sp,
            fontWeight=FontWeight.Normal
        ),
        headlineLarge=TextStyle(
            fontFamily=FontFamily.Serif,fontSize=27.sp,lineHeight=34.sp
        ),
        headlineMedium=TextStyle(
            fontFamily=FontFamily.Serif,fontSize=23.sp,lineHeight=30.sp
        ),
        titleLarge=TextStyle(
            fontFamily=FontFamily.Serif,fontSize=21.sp,lineHeight=28.sp
        ),
        titleMedium=TextStyle(
            fontFamily=FontFamily.SansSerif,fontSize=16.sp,lineHeight=23.sp,
            fontWeight=FontWeight.SemiBold
        ),
        bodyLarge=TextStyle(
            fontFamily=FontFamily.SansSerif,fontSize=16.sp,lineHeight=25.sp
        ),
        bodyMedium=TextStyle(
            fontFamily=FontFamily.SansSerif,fontSize=14.sp,lineHeight=22.sp
        ),
        bodySmall=TextStyle(
            fontFamily=FontFamily.SansSerif,fontSize=13.sp,lineHeight=20.sp
        ),
        labelLarge=TextStyle(
            fontFamily=FontFamily.SansSerif,fontSize=14.sp,lineHeight=20.sp,
            fontWeight=FontWeight.SemiBold
        ),
        labelMedium=TextStyle(
            fontFamily=FontFamily.SansSerif,fontSize=12.sp,lineHeight=18.sp,
            fontWeight=FontWeight.SemiBold
        ),
        labelSmall=TextStyle(
            fontFamily=FontFamily.SansSerif,fontSize=11.sp,lineHeight=17.sp,
            fontWeight=FontWeight.Bold
        )
    )

    @Composable
    fun pageHeadingStyle(): TextStyle {
        val cfg=LocalConfiguration.current
        val narrow=cfg.screenWidthDp < 360
        val largeScale=cfg.fontScale >= 1.6f
        return when {
            narrow && largeScale ->
                typography.headlineMedium.copy(fontSize=23.sp,lineHeight=30.sp)
            narrow -> typography.headlineLarge
            largeScale -> typography.displayMedium
            else -> typography.displayMedium
        }
    }

    @Composable
    fun sectionHeadingStyle(): TextStyle {
        val cfg=LocalConfiguration.current
        return if(cfg.screenWidthDp < 350 && cfg.fontScale >= 1.6f)
            typography.titleLarge else typography.headlineMedium
    }
}

internal object EvoluneFormatting {
    /**
     * The stored schedule zone remains authoritative; caller supplies LocalDate in
     * that zone. Only display text changes when the system locale changes.
     */
    fun date(date: LocalDate, pattern: String = "EEE, d MMM"): String =
        date.format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))

    fun clock(time: LocalTime): String =
        time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            .withLocale(Locale.getDefault()))

    fun uppercase(text: String): String = text.uppercase(Locale.getDefault())
}

@Composable
internal fun EvoluneEyebrow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = EvoluneTokens.Forest
) {
    Text(
        EvoluneFormatting.uppercase(text),
        modifier=modifier,
        style=EvoluneType.typography.labelSmall,
        letterSpacing=0.8.sp,
        color=color,
        softWrap=true
    )
}

@Composable
internal fun EvolunePageHeading(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = EvoluneTokens.Ink,
    align: TextAlign? = null
) {
    Text(
        text,
        modifier=modifier,
        style=EvoluneType.pageHeadingStyle(),
        color=color,
        textAlign=align,
        softWrap=true
    )
}

@Composable
internal fun EvoluneBody(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = EvoluneTokens.Muted
) {
    Text(
        text,
        modifier=modifier,
        style=EvoluneType.typography.bodyMedium,
        color=color,
        softWrap=true
    )
}
