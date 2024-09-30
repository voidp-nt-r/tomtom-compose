package com.tomtom.maps.compose

import androidx.compose.runtime.ComposableTargetMarker

/**
 * Custom Composable annotation used exclusively within a [TomTomMap] lambda block.
 *
 * This annotation serves two primary purposes:
 *
 * It triggers warnings at build time when a [TomTomMapComposable] element is used outside of the
 * main lambda composable block of [TomTomMap]. This helps ensure that composables are used
 * correctly and consistently within the intended scope.
 * Likewise, warnings will appear when a lambda that doesn't belong to [TomTomMapComposable] is used
 * inside the [TomTomMap] content block.
 */
@Retention(AnnotationRetention.BINARY)
@ComposableTargetMarker(description = "Exclusive TomTom maps composable")
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER
)
annotation class TomTomMapComposable