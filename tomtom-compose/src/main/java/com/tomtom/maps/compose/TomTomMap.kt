package com.tomtom.maps.compose

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.ui.MapView
import com.tomtom.sdk.map.display.ui.compass.CompassButton
import com.tomtom.sdk.map.display.ui.currentlocation.CurrentLocationButton
import com.tomtom.sdk.map.display.ui.logo.LogoView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

/**
 * Composable to create a MapView of TomTomMap.
 *
 * @param modifier [Modifier] to be applied to the TomTomMapView composable node.
 * @param mapOptions [MapOptions] to use for initializing the map.
 * @param cameraState
 * @param logoVisibilityPolicy Defines the visibility of the TomTom logo on the map.
 * @param currentLocationButtonVisibilityPolicy Defines the visibility of the current location
 * button.
 * @param compassButtonVisibilityPolicy Defines the visibility of the compass on map.
 * @param showZoomControl Determine whether th zoom control buttons are visible when true and hidden
 * when false.
 * @param content A [TomTomMapComposable] function that defines the map components to be shown on
 * the map.
 *
 * @see MapOptions
 * @see TomTomCameraState
 * @see LogoView.VisibilityPolicy
 * @see CurrentLocationButton.VisibilityPolicy
 * @see CompassButton.VisibilityPolicy
 */
@Composable
fun TomTomMap(
    modifier: Modifier = Modifier,
    mapOptions: MapOptions,
    cameraState: TomTomCameraState,
    logoVisibilityPolicy: LogoView.VisibilityPolicy = LogoView.VisibilityPolicy.Visible,
    currentLocationButtonVisibilityPolicy: CurrentLocationButton.VisibilityPolicy = CurrentLocationButton.VisibilityPolicy.Visible,
    compassButtonVisibilityPolicy: CompassButton.VisibilityPolicy = CompassButton.VisibilityPolicy.InvisibleWhenNorthUp,
    showZoomControl: Boolean = false,
    content: @Composable @TomTomMapComposable () -> Unit
) {
    val parentCompositionScope = rememberCoroutineScope()
    val parentComposition = rememberCompositionContext()
    val currentLifecycle = LocalLifecycleOwner.current.lifecycle
    var subCompositionJob by remember { mutableStateOf<Job?>(null) }
    var mapLifecycle: LifecycleEventObserver? = null

    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(
                context = context,
                mapOptions = mapOptions
            ).apply {
                logoView.visibilityPolicy = logoVisibilityPolicy
                currentLocationButton.visibilityPolicy = currentLocationButtonVisibilityPolicy
                compassButton.visibilityPolicy = compassButtonVisibilityPolicy
                zoomControlsView.isVisible = showZoomControl
            }.also { mapView: MapView ->

                mapLifecycle = mapView.lifecycleEventObserver()
                currentLifecycle.addObserver(mapLifecycle!!)

            }
        },
        update = { mapView ->
            mapView.getMapAsync { tomtomMap ->
                if (subCompositionJob == null) {
                    subCompositionJob = parentCompositionScope.launchSubComposition(
                        map = tomtomMap,
                        mapView = mapView,
                        cameraState = cameraState,
                        parentComposition = parentComposition,
                        content = content
                    )
                }
            }
        },
        onRelease = { _ ->
            mapLifecycle?.let { currentLifecycle.removeObserver(it) }
        }
    )

}

private fun CoroutineScope.launchSubComposition(
    map: TomTomMap,
    mapView: MapView,
    cameraState: TomTomCameraState,
    parentComposition: CompositionContext,
    content: @Composable () -> Unit
): Job {
    return launch(start = CoroutineStart.UNDISPATCHED) {
        val composition = Composition(
            applier = TomTomMapApplier(map, mapView),
            parent = parentComposition
        )
        try {
            composition.setContent {
                CameraUpdater(
                    cameraState = cameraState,
                )
                CompositionLocalProvider(
                    content = content
                )
            }
            awaitCancellation()
        } finally {
            composition.dispose()
            mapView.onDestroy()
        }

    }
}

internal fun MapView.lifecycleEventObserver(): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> this.onCreate(Bundle())
            Lifecycle.Event.ON_START -> this.onStart()
            Lifecycle.Event.ON_RESUME -> this.onResume()
            Lifecycle.Event.ON_PAUSE -> this.onPause()
            Lifecycle.Event.ON_STOP -> this.onStop()
            Lifecycle.Event.ON_DESTROY -> {} // called on the disposable of composition scope.
            else -> error("$event lifecycle is no supported.")
        }
    }
