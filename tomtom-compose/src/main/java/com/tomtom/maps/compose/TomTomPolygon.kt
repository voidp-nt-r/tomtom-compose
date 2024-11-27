package com.tomtom.maps.compose

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.image.Image
import com.tomtom.sdk.map.display.polygon.Polygon
import com.tomtom.sdk.map.display.polygon.PolygonOptions

/**
 * Node of [TomTomMapApplier] that is associated with a [Polygon].
 *
 * @property polygon The [Polygon] object that this node represents.
 * @property onClickListener Lambda function to be executed when clicked.
 */
internal class TomTomPolygonNode(
    val polygon: Polygon,
    var onClickListener: ((Polygon) -> Unit)?
) : TomTomMapNode {

    override fun onRemove() {
        polygon.remove()
    }

    override fun equals(other: Any?): Boolean =
        when(other) {
            is TomTomPolylineNode -> {
                this === other
            }
            is Polygon -> {
                this.polygon.id == other.id
            }
            else -> false
        }
}

/**
 * Composable to put a [Polygon] area on the [TomTomMap].
 *
 * @param coordinates Coordinates to be used to draw the perimeter of the [Polygon]. The edges of
 * the polygon will bw drawn in the coordinates order.
 * @param coordinatesOrder The [PolygonCoordinatesOrder] took to draw the polygon perimeter.
 * @param outlineColor Outline [Color] of the [Polygon] overlay.
 * @param outlineWidth Width of the outline of the [Polygon] overlay.
 * @param fillColor Main [Color] of the [Polygon].
 * @param isVisible Determinate whether the [PolygonalArea] is visible when true or hidden when
 * false.
 * @param image The [Image] that will be displayed inside of the [Polygon].
 * @param isImageOverlay If true, the [Image] is scaled to cover the whole polygon.
 * If false, the original size is preserved and is repeated starting
  *from the South-West corner of the polygon's bounding rectangle.
 * @param isClickable Sets if the polygon is clickable, if this parameter is false then clicks
 * will be propagated to objects that are below polygon.
 * @param onClickListener Lambda function to be executed when [Polygon] is clicked. This function
 * receives the clicked [Polygon] as a parameter.
 * @param tag Can be used to identify group of polygons.
 *
 * @sample com.tomtom.maps.compose.samples.PolygonalAreaSimpleSample
 *
 * @see Polygon
 * @see PolygonOptions
 */
@Composable
@TomTomMapComposable
fun PolygonalArea(
    coordinates: List<GeoPoint>,
    coordinatesOrder: PolygonCoordinatesOrder = PolygonCoordinatesOrder.DEFINED,
    outlineColor: Color = Color(PolygonOptions.DEFAULT_OUTLINE_COLOR),
    @FloatRange(from = 0.0) outlineWidth: Double = PolygonOptions.DEFAULT_OUTLINE_WIDTH,
    fillColor: Color = Color(PolygonOptions.DEFAULT_FILL_COLOR),
    isVisible: Boolean = true,
    image: Image? = null,
    isImageOverlay: Boolean = false,
    isClickable: Boolean = true,
    onClickListener: ((Polygon) -> Unit)? = null,
    tag: String? = null
) {
    val mapApplier = currentComposer.applier as? TomTomMapApplier
    ComposeNode<TomTomPolygonNode, TomTomMapApplier>(
        factory = {
            val polygonOptions = PolygonOptions(
                coordinates = when(coordinatesOrder) {
                    PolygonCoordinatesOrder.DEFINED -> coordinates
                    PolygonCoordinatesOrder.CONVEX_HULL -> convexHull(coordinates)
                },
                outlineColor = outlineColor.toArgb(),
                outlineWidth = outlineWidth,
                fillColor = fillColor.toArgb(),
                image = image,
                isImageOverlay = isImageOverlay,
                isClickable = isClickable,
                tag = tag
            )
            val polygon = mapApplier?.map?.addPolygon(polygonOptions)
                ?: error("Error adding polygonal area")
            TomTomPolygonNode(polygon = polygon, onClickListener = onClickListener)
        },
        update = {
            update(coordinates) { this.polygon.coordinates = it }
            update(outlineColor) { this.polygon.outlineColor = it.toArgb() }
            update(outlineWidth) { this.polygon.outlineWidth = it }
            update(fillColor) { this.polygon.fillColor = it.toArgb() }
            update(isVisible) { this.polygon.isVisible = it }
            update(image) { it?.let { img -> this.polygon.updateImage(img) } }
            update(isImageOverlay) { this.polygon.isImageOverlay = it }
            update(isClickable) { this.polygon }
            update(onClickListener) { this.onClickListener = it }
            update(tag) { this.polygon.tag = it }
        }
    )
}

/**
 * Enum class representing the order in which polygon coordinates are processed
 * when drawing a polygon on a map.
 *
 * @property DEFINED The order of the given coordinates should be used without any modifications. If
 * the coordinates order is unknown the resulting polygon may be distorted or twisted.
 * @property CONVEX_HULL Calculated a convex hull with the given coordinates. This is useful when
 * the order of coordinates is uncertain, ensuring properly defined perimeter.
 */
enum class PolygonCoordinatesOrder {
    DEFINED,
    CONVEX_HULL
}