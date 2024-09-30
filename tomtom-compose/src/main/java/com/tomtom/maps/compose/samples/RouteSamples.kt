package com.tomtom.maps.compose.samples

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tomtom.maps.compose.Route
import com.tomtom.quantity.Distance
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.route.Instruction

@Composable
internal fun RouteSimpleSample() {
    val color = Color(0f,0f,1f)
    Route(
        geometry = listOf(
            GeoPoint(52.377956, 4.897070),
            GeoPoint(51.377956, 4.997070),
            GeoPoint(50.377956, 5.897070),
            GeoPoint(52.377956, 5.897070),
        ),
        color = color,
        outlineWidth = 3.0,
        progress = Distance.meters(1000.0),
        instructions =
        listOf(
            Instruction(
                routeOffset = Distance.meters(1000.0),
                combineWithNext = false,
            ),
            Instruction(
                routeOffset = Distance.meters(2000.0),
                combineWithNext = true,
            ),
            Instruction(routeOffset = Distance.meters(3000.0)),
        ),
        tag = "Extra information about the route",
        departureMarkerVisible = true,
        destinationMarkerVisible = true,
    )
}