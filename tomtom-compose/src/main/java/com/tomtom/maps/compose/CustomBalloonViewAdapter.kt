package com.tomtom.maps.compose

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.tomtom.sdk.map.display.marker.BalloonViewAdapter
import com.tomtom.sdk.map.display.marker.Marker

/**
 * Implementation of the [BalloonViewAdapter] interface.
 * This adapter is responsible for creating a custom balloon view for a given marker in a map.
 *
 * @param context The context in which the balloon view will be created.
 * @param findLayout A lambda function that takes a [Marker] and returns a [Composable] function,
 * which defines the layout of the balloon view.
 *
 * @see BalloonViewAdapter
 */
internal class CustomBalloonViewAdapter(
    private val context: Context,
    private val findLayout: (Marker) -> (@Composable (Marker) -> Unit)?
) : BalloonViewAdapter {

    /**
     * Creates a balloon view for the specified marker.
     *
     * @param marker The [Marker] for which the balloon view is being created.
     * @return A [View] that represents the balloon, containing the Composable content
     * defined by the layout returned from findLayout.
     */
    override fun onCreateBalloonView(marker: Marker): View =
        ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                findLayout(marker)?.invoke(marker)
            }
        }

}