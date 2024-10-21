package com.tomtom.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.annotation.ExperimentalCameraApi
import com.tomtom.sdk.map.display.camera.CameraController
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.camera.CameraPosition
import kotlin.time.Duration

/**
 * Function to create a [TomTomCameraState] to manage the camera of a [TomTomMap] view.
 *
 * @param initState Lambda function to initialize the camara state.
 *
 * @return A [TomTomCameraState] that can be used to set and update the position of the camera of
 * a [TomTomMap] view.
 *
 * @see CameraOptions
 * @see Duration
 */
@Composable
inline fun rememberCameraState(
    crossinline initState: TomTomCameraState.() -> Unit = {}
): TomTomCameraState = rememberSaveable(saver = TomTomCameraState.Saver) {
    TomTomCameraState().apply(initState)
}

/**
 * Class to create state objects of the MapView camera.
 * An instance of this class may only be used for a single [TomTomMap] at time.
 *
 * @param initialCameraOptions Initial [CameraOptions] for the camera.
 * @param initialAnimationDuration [Duration] of the animations when camera position is modified.
 *
 * @see CameraOptions
 * @see Duration
 */
class TomTomCameraState(
    initialCameraOptions: CameraOptions = CameraOptions(),
    initialAnimationDuration: Duration = CameraController.DEFAULT_ANIMATION_DURATION
) {

    var cameraOptions: CameraOptions = initialCameraOptions
        set(value) {
            field = value
            map?.animateCamera(
                options =  this.cameraOptions,
                animationDuration = this.animationDuration
            )
            field = value
        }

    internal var cameraPosition by mutableStateOf(cameraOptions.position)

    private var map: TomTomMap? = null

    internal fun setMap(map: TomTomMap?) {
        this.map = map
    }

    var animationDuration: Duration = initialAnimationDuration

    val currentPosition
        get() = this.cameraPosition


    companion object {
        val Saver = Saver<TomTomCameraState, CameraOptions>(
            save = { CameraOptions(position = it.currentPosition) },
            restore = { TomTomCameraState(it) }
        )
    }
}

/**
 * Compose node to be used to link the [TomTomCameraState] to the [TomTomMap].
 *
 * @property map A [TomTomMap] to which the camera state will be associated.
 * @property cameraState The camera state to be linked to the [TomTomMap]
 */
internal class TomTomCameraNode(
    val map: TomTomMap?,
    var cameraState: TomTomCameraState
) : TomTomMapNode {

    init {
        cameraState.setMap(map)
    }

    private val updateCameraProps = { properties: CameraPosition ->
        cameraState.cameraPosition = properties.position
    }

    @OptIn(ExperimentalCameraApi::class)
    override fun onAttached() {
        map?.addCameraPropertiesChangeListener(updateCameraProps)
        map?.addCameraPropertiesSteadyListener(updateCameraProps)
    }

    override fun onCleared() {
        cameraState.setMap(null)
    }

    override fun onRemove() {
        cameraState.setMap(null)
    }

}

/**
 * Composable to manage the sate of the camera and prevent recomposition on the main map view,
 * updating only the camera values on state change.
 *
 * @param cameraState The state of the camera to observe for updates.
 *
 * @see TomTomCameraState
 * @see TomTomCameraNode
 */
@Composable
fun CameraUpdater(
    cameraState: TomTomCameraState,
) {
    val mapApplier = currentComposer.applier as? TomTomMapApplier
    val map = mapApplier?.map ?: error("Error setting camera")
    ComposeNode<TomTomCameraNode, TomTomMapApplier>(
        factory = {
            TomTomCameraNode(
                map = map,
                cameraState = cameraState
            )
        },
        update = {
            update(cameraState) {
                this.cameraState = it
            }
        }
    )
}