package com.tomtom.maps.compose

import androidx.compose.runtime.Composable
import com.tomtom.sdk.location.GeoPoint

@Composable
@TomTomMapComposable
private fun Cluster(
    coordinates: List<GeoPoint>
    //content: TomTomScope.() -> Unit
) {
    val centroid = findMiddleCoordinate(coordinates)
    val radius = coordinates.maxOf { centroid.distanceTo(it) }
    /*CircularArea(
        coordinate = centroid,
        radius = Radius(value = radius.inMeters(), unit = RadiusUnit.Meters),
        fillColor = Color.Blue.copy(alpha = 0.3f)
    )*/
    val hull = convexHull(coordinates)
    PolygonalArea(
        coordinates = hull
    )
}

