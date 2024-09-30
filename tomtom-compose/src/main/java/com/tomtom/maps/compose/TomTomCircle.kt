package com.tomtom.maps.compose

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.circle.Circle
import com.tomtom.sdk.map.display.circle.CircleOptions
import com.tomtom.sdk.map.display.circle.Radius

/**
 * Node of [TomTomMapApplier] that is associated to [Circle]
 *
 * @property circle The [Circle] object that this node represents.
 * @property onClickListener Lambda function to be executed when kept pressed.
 */
internal class TomTomCircleNode(
    val circle: Circle,
    val onClickListener: ((Circle) -> Unit)?
) : TomTomMapNode {

    override fun onRemove() {
        circle.remove()
    }

    override fun equals(other: Any?): Boolean =
        when(other) {
            is TomTomCircleNode -> {
                this === other
            }
            is Circle -> {
                this.circle.coordinate == other.coordinate
            }
            else -> false
        }

}

/**
 * Composable to put a [Circle] area in on the [TomTomMap].
 *
 * @param coordinate Center of the circle overlay.
 * @param radius [Radius] of the circle overlay. Radius must be greater than 0.
 * @param fillColor [Color] of the circle overlay.
 * @param outlineColor [Color] for the circle outline overlay.
 * @param isVisible Determinate whether the [CircularArea] is visible when true and hidden when
 * false.
 * @param outlineRadius Radius of the circle outline overlay, use the same units as specified in
 * [radius].
 * @param isClickable Sets if the circle is clickable, if this parameter is false then clicks
 * will be propagated to objects that are below circle.
 * @param onClickListener Lambda function to be executed when [CircularArea] is clicked. This
 * function receives the clicked [Circle] as a parameter.
 * @param tag Can be used to identify group of circles.
 *
 * @sample com.tomtom.maps.compose.samples.CircularAreaSimpleSample
 *
 * @see Circle
 * @see CircleOptions
 */
@Composable
@TomTomMapComposable
fun CircularArea(
    coordinate: GeoPoint,
    radius: Radius,
    fillColor: Color = Color(CircleOptions.DEFAULT_FILL_COLOR),
    outlineColor: Color = Color(CircleOptions.DEFAULT_OUTLINE_COLOR),
    isVisible: Boolean = true,
    @FloatRange(from = 0.0) outlineRadius: Double = CircleOptions.DEFAULT_OUTLINE_RADIUS,
    isClickable: Boolean = true,
    onClickListener: ((Circle) -> Unit)? = null,
    tag: String? = null
) {
    val mapApplier = currentComposer.applier as? TomTomMapApplier
    ComposeNode<TomTomCircleNode, TomTomMapApplier>(
        factory = {
            val circleOptions = CircleOptions(
                coordinate = coordinate,
                radius = radius,
                fillColor = fillColor.toArgb(),
                outlineColor = outlineColor.toArgb(),
                outlineRadius = outlineRadius,
                isClickable = isClickable,
                tag = tag
            )
            val circle = mapApplier?.map?.addCircle(circleOptions)
                ?: error("Error adding radius map area to the map")
            TomTomCircleNode(circle = circle, onClickListener = onClickListener)
        },
        update = {
            update(coordinate) { this.circle.coordinate = it }
            update(radius) { this.circle.radius = it }
            update(fillColor) { this.circle.fillColor = it.toArgb() }
            update(outlineColor) { this.circle.outlineColor = it.toArgb() }
            update(outlineRadius) { this.circle.outlineRadius = it }
            update(isVisible) { this.circle.isVisible = it }
        }
    )

}