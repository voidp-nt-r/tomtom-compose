package com.tomtom.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.gesture.MapClickListener
import com.tomtom.sdk.map.display.gesture.MapDoubleClickListener
import com.tomtom.sdk.map.display.gesture.MapLongClickListener
import com.tomtom.sdk.map.display.gesture.MapPanningListener

/**
 * Compose node to manage the gesture listener of [TomTomMap].
 *
 * @property map A [TomTomMap] to which the gesture listeners will be associated.
 *
 * @param initialClickListener The initial click listener gesture of the map.
 * @param initialDoubleClickListener The initial double click listener gesture of the map.
 * @param initialLongClickListener The initial long click listener gesture of the map.
 * @param initialPanningListener The initial panning listener gesture of the map.
 */
internal class TomTomGestureListenerNode(
    val map: TomTomMap,
    initialClickListener: MapClickListener?,
    initialDoubleClickListener: MapDoubleClickListener?,
    initialLongClickListener: MapLongClickListener?,
    initialPanningListener: MapPanningListener?
) : TomTomMapNode {

    internal var clickListener = initialClickListener
        set(value) {
            this.clickListener?.let { this.map.removeMapClickListener(it) }
            field = value
            value?.let { this.map.addMapClickListener(it) }
        }

    internal var doubleClickListener = initialDoubleClickListener
        set(value) {
            this.doubleClickListener?.let { this.map.removeMapDoubleClickListener(it) }
            field = value
            value?.let { this.map.addMapDoubleClickListener(it) }
        }

    internal var longClickListener = initialLongClickListener
        set(value) {
            this.longClickListener?.let { this.map.removeMapLongClickListener(it) }
            field = value
            value?.let { this.map.addMapLongClickListener(it) }
        }

    internal var panningListener = initialPanningListener
        set(value) {
            this.panningListener?.let { this.map.removeMapPanningListener(it) }
            field = value
            value?.let { this.map.addMapPanningListener(it) }
        }

    private fun setGestureListeners() {
        this.clickListener?.let { this.map.addMapClickListener(it) }
        this.doubleClickListener?.let { this.map.addMapDoubleClickListener(it) }
        this.longClickListener?.let { this.map.addMapLongClickListener(it) }
        this.panningListener?.let { this.map.addMapPanningListener(it) }
    }

    override fun onAttached() {
        this.setGestureListeners()
    }

    override fun onCleared() {
    }

    override fun onRemove() {
    }

}

/**
 * Composable to manage the gesture listeners of the map and to prevent recomposition on the main
 * map view, updating only the gesture listeners.
 *
 * @param clickListener [MapClickListener] to execute when map is clicked.
 * @param doubleClickListener [MapDoubleClickListener] to execute when map is double clicked.
 * @param longClickListener [MapLongClickListener] to execute when map is long clicked.
 * @param panningListener [MapPanningListener] to execute when map has a panning gesture.
 */
@Composable
internal fun GestureListenerUpdater(
    clickListener: MapClickListener?,
    doubleClickListener: MapDoubleClickListener?,
    longClickListener: MapLongClickListener?,
    panningListener: MapPanningListener?,
) {
    val mapApplier = currentComposer.applier as? TomTomMapApplier
    val map = mapApplier?.map ?: error("Error setting map view event listeners")
    ComposeNode<TomTomGestureListenerNode, TomTomMapApplier>(
        factory = {
            TomTomGestureListenerNode(
                map = map,
                initialClickListener = clickListener,
                initialDoubleClickListener = doubleClickListener,
                initialLongClickListener = longClickListener,
                initialPanningListener = panningListener
            )
        },
        update = {
            update(clickListener) {
                this.clickListener = it
            }

            update(doubleClickListener) {
                this.doubleClickListener = it
            }

            update(longClickListener) {
                this.longClickListener = it
            }

            update(panningListener) {
                this.panningListener = it
            }
        }
    )
}