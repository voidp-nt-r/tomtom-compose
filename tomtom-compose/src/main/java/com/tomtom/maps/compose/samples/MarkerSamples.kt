package com.tomtom.maps.compose.samples

import com.tomtom.maps.compose.Marker

import androidx.compose.runtime.Composable
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.image.ImageFactory

@Composable
internal fun MarkerSimpleSample() {
    Marker(
        coordinate = GeoPoint(52.379189, 4.89943),
        pinImage = ImageFactory.fromResource(0),
        onClickListener = { marker ->
            // do something.
        },
        onLongClickListener = { marker ->
            // do something.
        }
    )
}

@Composable
internal fun MarkerBalloonSample() {
    Marker(
        coordinate = GeoPoint(52.379189, 4.89943),
        pinImage = ImageFactory.fromResource(0),
        onClickListener = { marker ->
            if(marker.isSelected()) {
                marker.deselect()
            }
            else {
                marker.select()
            }
        },
        balloonText = "marker text",
        balloonView = { marker ->
            //Text(text = marker.balloonText)
        }
    )
}
