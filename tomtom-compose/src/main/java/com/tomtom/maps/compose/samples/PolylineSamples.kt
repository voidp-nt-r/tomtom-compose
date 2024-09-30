package com.tomtom.maps.compose.samples

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tomtom.maps.compose.Polyline
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.common.WidthByZoom
import com.tomtom.sdk.map.display.polyline.CapType

@Composable
internal fun PolylineSimpleSample() {
    val lineColor = Color(0.2f, 0.6f, 1f, 1f)
    val outlineColor = Color(0f, 0.3f, 0.5f, 1f)
    Polyline(
        coordinates = listOf(
            GeoPoint(latitude = 52.33744437330409, longitude = 4.84036333215833),
            GeoPoint(latitude = 52.3374581784774, longitude = 4.88185047814447),
            GeoPoint(latitude = 52.32935816673911, longitude = 4.910078096170823),
        ),
        lineColor = lineColor,
        lineWidths = listOf(WidthByZoom(10.0)),
        outlineColor = outlineColor,
        outlineWidths = listOf(WidthByZoom(1.0)),
        lineStartCapType = CapType.InverseDiamond,
        lineEndCapType = CapType.Diamond,
    )
}