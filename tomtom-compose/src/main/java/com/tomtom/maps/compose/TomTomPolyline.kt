package com.tomtom.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.common.WidthByZoom
import com.tomtom.sdk.map.display.image.Image
import com.tomtom.sdk.map.display.polyline.CapType
import com.tomtom.sdk.map.display.polyline.Polyline
import com.tomtom.sdk.map.display.polyline.PolylineOptions

/**
 * Node of [TomTomMapApplier] that is associated to a [Polyline]
 *
 * @property polyline The [Polyline] object that this node represents.
 * @property onClickListener Lambda function to be executed when clicked.
 */
internal class TomTomPolylineNode(
    val polyline: Polyline,
    var onClickListener: ((Polyline) -> Unit)?
) : TomTomMapNode {

    override fun onRemove() {
        polyline.remove()
    }

    override fun equals(other: Any?): Boolean =
        when(other) {
            is Polyline -> {
                this.polyline.id == other.id
            }
            else -> false
        }

}

/**
 * Composable to draw a [Polyline] on the [TomTomMap].
 *
 * @param coordinates Coordinates of the line.
 * @param lineColor Main [Color] of the polyline.
 * @param lineWidths Widths of the polyline for different zoom levels.
 * @param outlineColor Outline [Color] for the polyline overlay.
 * @param outlineWidths Outline widths of the polyline for different zoom levels.
 * @param isVisible
 * @param lineStartCapType The [CapType] of the start cap of the [Polyline] to be created.
 * @param lineEndCapType The [CapType] of the end cap of the [Polyline] to be created.
 * @param isClickable Sets if the polyline is clickable, if this parameter is false then clicks
 * will be propagated to objects that are below polyline.
 * @param onClickListener Lambda function to be executed when the [Polyline] is clicked. The
 * function will receive the clicked [Polyline] as a parameter.
 * @param tag Can be used to identify group of polylines.
 * @param patternImage The pattern image of the polyline.
 * This image is repeated along the polyline, and it has priority over the solid color from [lineColor].
 *
 * @sample com.tomtom.maps.compose.samples.PolylineSimpleSample
 *
 * @see Polyline
 * @see PolylineOptions
 */
@Composable
fun Polyline(
    coordinates: List<GeoPoint>,
    lineColor: Color = Color(PolylineOptions.DEFAULT_LINE_COLOR),
    lineWidths: List<WidthByZoom> = listOf(WidthByZoom(PolylineOptions.DEFAULT_OUTLINE_WIDTH)),
    outlineColor: Color = Color(PolylineOptions.DEFAULT_OUTLINE_COLOR),
    outlineWidths: List<WidthByZoom> = listOf(WidthByZoom(PolylineOptions.DEFAULT_OUTLINE_WIDTH)),
    isVisible: Boolean = true,
    lineStartCapType: CapType = CapType.None,
    lineEndCapType: CapType = CapType.None,
    isClickable: Boolean = true,
    onClickListener: ((Polyline) -> Unit)? = null,
    tag: String? = null,
    patternImage: Image? = null
) {
    val mapApplier = currentComposer.applier as? TomTomMapApplier
    ComposeNode<TomTomPolylineNode, TomTomMapApplier>(
        factory = {
            val polylineOptions = PolylineOptions(
                coordinates = coordinates,
                lineColor = lineColor.toArgb(),
                lineWidths = lineWidths,
                outlineColor = outlineColor.toArgb(),
                outlineWidths = outlineWidths,
                lineStartCapType = lineStartCapType,
                lineEndCapType = lineEndCapType,
                isClickable = isClickable,
                tag = tag,
                patternImage = patternImage
            )
            val polyline = mapApplier?.map?.addPolyline(polylineOptions)
                ?: error("Error adding a polyline")
            TomTomPolylineNode(polyline = polyline, onClickListener = onClickListener)
        },
        update = {
            update(coordinates) { this.polyline.coordinates = it }
            update(lineColor) { this.polyline.lineColor = it.toArgb() }
            update(lineWidths) { this.polyline.lineWidths = it }
            update(outlineColor) { this.polyline.outlineColor = it.toArgb() }
            update(outlineWidths) { this.polyline.outlineWidths = it }
            //update(lineStartCapType) { this.polyline }
            //update(lineEndCapType) {}
            update(isVisible) { this.polyline.isVisible = it }
            update(isClickable) { this.polyline }
            update(onClickListener) { this.onClickListener = it }
            //update(tag) { this.polyline }
            update(patternImage) { this.polyline.setPatternImage(it) }
        }
    )

}