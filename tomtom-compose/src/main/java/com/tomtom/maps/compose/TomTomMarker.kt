package com.tomtom.maps.compose

import android.graphics.PointF
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.image.Image
import com.tomtom.sdk.map.display.marker.Label
import com.tomtom.sdk.map.display.marker.Marker
import com.tomtom.sdk.map.display.marker.MarkerOptions

/**
 * Node of [TomTomMap] that is associated with a [Marker].
 *
 * @property marker The [Marker] that this node represents.
 * @property onClickListener Lambda function to be executed when clicked.
 * @property onLongClickListener Lambda function to be executed when kept pressed.
 * @property balloonView [Composable] lambda to associate to the marker as a balloon view.
 */
internal class TomTomMarkerNode(
    val marker: Marker,
    var onClickListener: ((Marker) -> Unit)?,
    var onLongClickListener: ((Marker) -> Unit)?,
    var balloonView: (@Composable (Marker) -> Unit)?
) : TomTomMapNode {

    override fun onAttached() {
    }

    override fun onRemove() {
        if (marker.isSelected()) { // deselect the marker to avoid problems with the custom balloonView
            marker.deselect()
        }
        marker.remove()
    }

    override fun onCleared() {
        marker.remove()
    }

    override fun equals(other: Any?): Boolean =
        when (other) {
            is TomTomMarkerNode -> {
                this === other
            }
            is Marker -> {
                this.marker.id == other.id
            }
            else -> false
        }

}

/**
 * Composable to put a [Marker] on the [TomTomMap].
 *
 * @param coordinate Location of the marker.
 * @param pinImage [Image] that will be used to represent the marker.
 * @param pinIconImage [Image] that will be used as a pin icon.
 * @param shieldImage [Image] that will be used as a marker shield.
 * @param placementAnchor Sets the whole marker offset.
 * @param pinIconAnchor Sets the position of the icon relative to the pin. When the given x/ y
 * values are outside of the range 0, 1, the icon moves more than the width/ height of the pin.
 * @param shieldImageAnchor Sets the shield Image offset.
 * @param isVisible Determinate whether the [Marker] is visible when true and hidden when false.
 * @param onClickListener Lambda function to be executed when [Marker] is clicked. This function
 * receives the clicked [Marker] as a parameter.
 * @param onLongClickListener Lambda function to be executed when the [Marker] is kept pressed. This
 * function receives the kept pressed [Marker] as a parameter.
 * @param tag [String] that can help to identify a group of markers.
 * @param label [Label] that textually description for the marker.
 * @param balloonText [String] that will show inside markers balloon.
 * @param balloonView Optional [Composable] lambda that will be shown as a marker balloon. If this
 * value is null, a default balloon with [balloonText] as message will be displayed.
 *
 * @sample com.tomtom.maps.compose.samples.MarkerSimpleSample
 * @sample com.tomtom.maps.compose.samples.MarkerBalloonSample
 *
 * @see Marker
 * @see MarkerOptions
 */
@Composable
@TomTomMapComposable
fun Marker(
    coordinate: GeoPoint,
    pinImage: Image,
    pinIconImage: Image? = null,
    shieldImage: Image? = null,
    pinIconAnchor: PointF = Marker.DEFAULT_PIN_ICON_ANCHOR,
    placementAnchor: PointF = Marker.DEFAULT_PLACEMENT_ANCHOR,
    shieldImageAnchor: PointF = Marker.DEFAULT_SHIELD_IMAGE_ANCHOR,
    isVisible: Boolean = true,
    onClickListener: ((Marker) -> Unit)? = null,
    onLongClickListener: ((Marker) -> Unit)? = null,
    tag: String? = null,
    label: Label? = null,
    balloonText: String = "",
    balloonView: (@Composable (Marker) -> Unit)? = null
) {
    val mapApplier = currentComposer.applier as? TomTomMapApplier
    ComposeNode<TomTomMarkerNode, TomTomMapApplier>(
        factory = {
            val markerOptions = MarkerOptions(
                coordinate = coordinate,
                pinImage = pinImage,
                pinIconImage = pinIconImage,
                shieldImage = shieldImage,
                pinIconAnchor = pinIconAnchor,
                placementAnchor = placementAnchor,
                shieldImageAnchor = shieldImageAnchor,
                tag = tag,
                label = label,
                balloonText = balloonText
            )
            val marker = mapApplier?.map?.addMarker(markerOptions) ?: error("Error adding marker.")
            TomTomMarkerNode(
                marker = marker,
                onClickListener = onClickListener,
                onLongClickListener = onLongClickListener,
                balloonView = balloonView
            )
        },
        update = {
            update(coordinate) { this.marker.coordinate = it }
            update(pinImage) { this.marker.setPinImage(it) }
            update(pinIconImage) { this.marker.setPinIconImage(it) }
            update(shieldImage) { this.marker.setShieldImage(it) }
            update(pinIconAnchor) { this.marker.setPinIconAnchor(it) }
            update(placementAnchor) { this.marker.setPlacementAnchor(it) }
            update(shieldImageAnchor) { this.marker.setShieldAnchor(it) }
            update(onClickListener) { this.onClickListener = it }
            update(onLongClickListener) { this.onLongClickListener = it }
            update(tag) { this.marker.tag = it }
            update(label) { this.marker.setLabel(it) }
            //update(balloonText) {  }
            update(isVisible) { this.marker.isVisible = isVisible }
            update(balloonView) { this.balloonView = it }
        }
    )
}