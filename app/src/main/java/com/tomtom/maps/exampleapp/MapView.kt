package com.tomtom.maps.exampleapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tomtom.maps.compose.Marker
import com.tomtom.maps.compose.TomTomMap
import com.tomtom.maps.compose.rememberCameraState
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.image.ImageFactory

/**
 * This is a simple example of implementing the compose map. To see it in action, replace the map
 * options string with your API key.
 *
 * Note that using a string literal for your API key is not recommended.
 *
 * This example is for demonstration purposes only. For detailed instructions on correctly setting
 * up your API key in the project, please refer to the [documentation](https://developer.tomtom.com/maps/android/getting-started/project-setup).
 */
@Composable
fun MapView() {
    val cameraState = rememberCameraState()
    val mapOptions = MapOptions(mapKey = "REPLACE THIS STRING WITH YOUR API KEY")
    TomTomMap(
        modifier = Modifier.fillMaxSize(),
        mapOptions = mapOptions,
        cameraState = cameraState
    ) {
       Marker(
           coordinate = GeoPoint(52.379189, 4.899431),
           pinImage = ImageFactory.fromResource(R.drawable.baseline_location_pin_24)
       )
    }
}