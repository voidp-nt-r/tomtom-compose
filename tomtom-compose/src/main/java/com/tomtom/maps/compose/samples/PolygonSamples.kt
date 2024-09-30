package com.tomtom.maps.compose.samples

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tomtom.maps.compose.PolygonalArea
import com.tomtom.sdk.location.GeoPoint

@Composable
internal fun PolygonalAreaSimpleSample() {
    val fillColor = Color(0f, 0f, 1.0f, 0.5f)
    val outlineColor = Color(0f, 0f, 1.0f, 1.0f)
    PolygonalArea(
        coordinates = listOf(
            GeoPoint(latitude = 52.33744437330409, longitude = 4.84036333215833),
            GeoPoint(latitude = 52.3374581784774, longitude = 4.88185047814447),
            GeoPoint(latitude = 52.32935816673911, longitude = 4.910078096170823),
            GeoPoint(latitude = 52.381705486736315, longitude = 4.893630047460435),
            GeoPoint(latitude = 52.385294680380866, longitude = 4.846939597146335),
        ),
        outlineColor = outlineColor,
        outlineWidth = 2.0,
        fillColor = fillColor,
    )
}