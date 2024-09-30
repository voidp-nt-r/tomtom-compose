package com.tomtom.maps.compose

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.tomtom.quantity.Distance
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.common.WidthByZoom
import com.tomtom.sdk.map.display.image.Image
import com.tomtom.sdk.map.display.route.Instruction
import com.tomtom.sdk.map.display.route.Route
import com.tomtom.sdk.map.display.route.RouteOptions
import com.tomtom.sdk.map.display.route.RouteSection

/**
 * Node of [TomTomMapApplier] that is associated with a [Route].
 *
 * @param route The [Route] object that this node represents.
 * @param onClickListener Lambda function to be executed when clicked.
 */
internal class TomTomRouteNode(
    val route: Route,
    val onClickListener: ((Route) -> Unit)?
) : TomTomMapNode {

    override fun onAttached() {
    }

    override fun onRemove() {
        route.remove()
    }

    override fun onCleared() {
        route.remove()
    }

    override fun equals(other: Any?): Boolean =
        when(other) {
            is TomTomRouteNode -> {
                this === other
            }
            is Route -> {
                this.route.id == other.id
            }
            else -> false
        }

}

/**
 * Composable to put [Route] on the [TomTomMap].
 *
 * @param geometry List of [GeoPoint] that defines the [Route] path.
 * @param color [Color] of the route tube. Colors for the style of the route tube
 * (route, dotted line and outline colors) will be selected based on this color.
 * @param outlineWidth Width of the route outline in density-independent pixels (dp).
 * @param widths Widths of the route tube for different zoom levels.
 * If the same width should be applied for all zoom levels, then the zoom value can be
 * skipped in the [WidthByZoom] initialization.
 * @param visible Determines the visibility of the [Route].
 * @param progress Sets the route progress. The internal map mechanism automatically
 * renders the driven route section in a slightly different color which brings a pleasant user experience.
 * @param instructions List of [Instruction] for maneuvers along the [Route].
 * @param tag Optional holder for custom extra information about the requested [Route].
 * @param departureMarkerVisible Determines the visibility of the departure marker.
 * @param destinationMarkerVisible Determines the visibility of the destination marker.
 * @param isFollowable Determines whether this [Route] is followable or not.
 * A followable route is eligible to be followed by
 * [CameraTrackingMode.FollowRouteDirection][com.tomtom.sdk.map.display.camera.CameraTrackingMode.Companion.FollowRouteDirection].
 * Only one [Route] can be followed at a time. When newly added route is marked as followable,
 * the previous one stops being followable.
 * @param routeOffset a list of [Distance] instances that represent the offset along the route for each
 * point in the route's geometry. These offsets help in positioning instruction arrows and
 * determining route progress. The n-th element in `routeOffset` represents the distance from the start
 * of the route to the n-th point in the geometry. If not explicitly set, `routeOffset` is derived
 * from the distances between geometry points during the construction of `RouteOptions`.
 * @param sections Information about [sections][RouteSection] on the route.
 * @param departureMarkerPinImage [Image] that will be used as the departure marker pin. If you pass `null`, the default image is used.
 * @param destinationMarkerPinImage [Image] that will be used as the destination marker pin. If you pass `null`, the default image is used.
 * @param departure This specifies where the [departureMarkerPinImage] is located. If you pass `null`,
 * departure marker will be shown on the first point of the [geometry].
 * @param destination This specifies where the [destinationMarkerPinImage] is located. If you pass `null`,
 * the departure marker is shown on the last point of the [geometry].
 *
 * @sample com.tomtom.maps.compose.samples.RouteSimpleSample
 *
 * @see Route
 * @see RouteOptions
 */
@Composable
@TomTomMapComposable
fun Route(
    geometry: List<GeoPoint>,
    color: Color = Color(RouteOptions.DEFAULT_COLOR),
    @FloatRange(from = 0.0) outlineWidth: Double = RouteOptions.DEFAULT_OUTLINE_WIDTH,
    widths: List<WidthByZoom> = RouteOptions.DEFAULT_WIDTHS,
    visible: Boolean = true,
    progress: Distance = Distance.ZERO,
    instructions: List<Instruction> = emptyList(),
    tag: String? = null,
    departureMarkerVisible: Boolean = false,
    destinationMarkerVisible: Boolean = false,
    isFollowable: Boolean = false,
    routeOffset: List<Distance> = emptyList(),
    sections: List<RouteSection> = emptyList(),
    departureMarkerPinImage: Image? = null,
    destinationMarkerPinImage: Image? = null,
    departure: GeoPoint? = null,
    destination: GeoPoint? = null, onClickListener: ((Route) -> Unit)? = null
) {
    val mapApplier = currentComposer.applier as? TomTomMapApplier
    ComposeNode<TomTomRouteNode, TomTomMapApplier>(
        factory = {
            val routeOptions = RouteOptions(
                geometry = geometry,
                color = color.toArgb(),
                outlineWidth = outlineWidth,
                widths = widths,
                visible = visible,
                progress = progress,
                instructions = instructions,
                tag = tag,
                departureMarkerVisible = departureMarkerVisible,
                destinationMarkerVisible = destinationMarkerVisible,
                isFollowable = isFollowable,
                routeOffset = routeOffset,
                sections = sections,
                departureMarkerPinImage = departureMarkerPinImage,
                destinationMarkerPinImage = destinationMarkerPinImage,
                departure = departure,
                destination = destination
            )
            val route = mapApplier?.map?.addRoute(routeOptions) ?: error("Error on route drawing")
            TomTomRouteNode(
                route = route,
                onClickListener = onClickListener
            )
        },
        update = {

        }
    )

}