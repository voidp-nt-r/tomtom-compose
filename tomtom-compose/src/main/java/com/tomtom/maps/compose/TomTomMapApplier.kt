package com.tomtom.maps.compose

import androidx.compose.runtime.AbstractApplier
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.circle.Circle
import com.tomtom.sdk.map.display.marker.Marker
import com.tomtom.sdk.map.display.polygon.Polygon
import com.tomtom.sdk.map.display.polyline.Polyline
import com.tomtom.sdk.map.display.route.Route
import com.tomtom.sdk.map.display.ui.MapView
import kotlin.time.Duration.Companion.seconds

internal interface TomTomMapNode {
    fun onAttached() {}
    fun onRemove() {}
    fun onCleared() {}
}

private object NodeRoot : TomTomMapNode

internal class TomTomMapApplier(
    val map: TomTomMap,
    private val mapView: MapView
) : AbstractApplier<TomTomMapNode>(NodeRoot) {

    private val decorations = mutableListOf<TomTomMapNode>()

    init {
        setEventListeners()
        setBalloonAdapters()
    }

    override fun move(from: Int, to: Int, count: Int) {
        decorations.move(from, to, count)
    }

    override fun onClear() {
        map.clear()
        decorations.forEach { it.onCleared() }
        decorations.clear()
    }

    override fun remove(index: Int, count: Int) {
        repeat(count) {
            decorations[index + it].onRemove()
        }
        decorations.remove(index, count)
    }

    override fun insertTopDown(index: Int, instance: TomTomMapNode) {
    }

    override fun insertBottomUp(index: Int, instance: TomTomMapNode) {
        decorations.add(index, instance)
        instance.onAttached()
    }

    private fun setEventListeners() {
        map.addMarkerClickListener { marker ->
            decorations.executeCallBackFor<TomTomMarkerNode, Marker>(mapElement = marker) {
                onClickListener
            }
        }

        map.addMarkerLongClickListener { marker ->
            decorations.executeCallBackFor<TomTomMarkerNode, Marker>(mapElement = marker) {
                onLongClickListener
            }
        }

        map.addMarkerSelectionListener { marker, isSelected ->
            if(isSelected) {
                map.animateCamera(
                    CameraOptions(position = marker.coordinate),
                    animationDuration = 1.seconds
                )
            }
        }

        map.addCircleClickListener { circle ->
            decorations.executeCallBackFor<TomTomCircleNode, Circle>(mapElement = circle) {
                onClickListener
            }
        }

        map.addPolygonClickListener { polygon ->
            decorations.executeCallBackFor<TomTomPolygonNode, Polygon>(mapElement = polygon) {
                onClickListener
            }
        }

        map.addPolylineClickListener { polyline ->
            decorations.executeCallBackFor<TomTomPolylineNode, Polyline>(mapElement = polyline) {
                onClickListener
            }
        }

        map.addRouteClickListener{ route ->
            decorations.executeCallBackFor<TomTomRouteNode, Route>(mapElement = route) {
                onClickListener
            }
        }

    }

    private fun setBalloonAdapters() {
        mapView.markerBalloonViewAdapter = CustomBalloonViewAdapter(
            context = mapView.context,
            findLayout = { marker ->
                (decorations.find { node ->
                    node is TomTomMarkerNode && node.marker.id == marker.id
                } as TomTomMarkerNode).balloonView
            }
        )
    }

}

/**
 * Extension function for [Iterable] objects to find the associated callback to the compose node.
 *
 * @param mapElement [I] map element given by the map controller.
 * @param execute Lambada function from the [NodeT] compose node associated to the map element.
 */
private inline fun <reified NodeT : TomTomMapNode, I> Iterable<TomTomMapNode>.executeCallBackFor(
    mapElement: I,
    execute: NodeT.() -> ((I) -> Unit)?
) {
    this.forEach { node ->
        if (node is NodeT) {
            if (node == mapElement) {
                execute(node)?.invoke(mapElement)
            }
        }
    }
}