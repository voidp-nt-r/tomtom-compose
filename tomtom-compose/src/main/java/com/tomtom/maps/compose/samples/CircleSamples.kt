package com.tomtom.maps.compose.samples

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tomtom.maps.compose.CircularArea
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.circle.Radius
import com.tomtom.sdk.map.display.circle.RadiusUnit

@Composable
internal fun CircularAreaSimpleSample() {
    val fillColor = Color(0.99f, 0.93f, 0.83f, 0.5f)
    val outlineColor = Color(0.93f, 0.62f, 0f, 0.6f)
    CircularArea(
        GeoPoint(52.367956, 4.897070),
        radius = Radius(3000.0, RadiusUnit.Meters),
        fillColor = fillColor,
        outlineColor = outlineColor,
        outlineRadius = 100.0,
    )
}