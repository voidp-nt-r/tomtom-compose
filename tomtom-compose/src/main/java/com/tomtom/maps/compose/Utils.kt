package com.tomtom.maps.compose

import com.tomtom.quantity.Angle
import com.tomtom.sdk.location.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Data class to express Geodesic coordinates in a Euclidean space.
 *
 * @property x The x-coordinate, representing the horizontal position in 3D space.
 * @property y The y-coordinate, representing the vertical position in 3D space.
 * @property z The z-coordinate, representing the depth position in 3D space.
 */
internal data class EuclideanCoordinates(
    val x: Double,
    val y: Double,
    val z: Double
) {

    operator fun plus(other: EuclideanCoordinates): EuclideanCoordinates =
        EuclideanCoordinates(
            x = this.x + other.x,
            y = this.y + other.y,
            z = this.z + other.z
        )

    operator fun div(denominator: Double): EuclideanCoordinates =
        EuclideanCoordinates(
            x = this.x / denominator,
            y = this.y / denominator,
            z = this.z / denominator
        )

    override fun equals(other: Any?): Boolean =
        when (other) {
            is EuclideanCoordinates -> {
                this.x == other.x && this.y == other.y && this.z == other.z
            }

            else -> false
        }

    /**
     * Converts the Euclidean coordinates to a GeoPoint representation.
     *
     * This function computes the geographical coordinates (latitude and longitude)
     * from the Euclidean coordinates (x, y, z) using spherical conversion.
     *
     * @return A [GeoPoint] object representing the geographical coordinates, where longitude is
     * derived from the x and y coordinates, and latitude is derived from the z coordinate and the
     * computed distance from the origin in the xy-plane.
     */
    fun toGeoPoint(): GeoPoint {
        val p = sqrt(this.x.pow(2) + this.y.pow(2))
        return GeoPoint(
            longitude = Angle.radians(atan2(this.y, this.x)).inDegrees(),
            latitude = Angle.radians(atan2(this.z, p)).inDegrees()
        )
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

}

/**
 * Function to find the middle point of a group of [GeoPoint] coordinates by calculate the mean of
 * all coordinates.
 *
 * @return The calculated [GeoPoint] that represents the average of all coordinates given.
 */
internal fun findMiddleCoordinate(coordinates: List<GeoPoint>): GeoPoint {
    val cardinality = coordinates.size.toDouble()
    val coordinatesMean = coordinates.map(GeoPoint::toEuclideanCoordinate).reduce { acc, geoPoint ->
        acc + (geoPoint / cardinality)
    }
    return coordinatesMean.toGeoPoint()
}

/**
 *
 */
internal fun convexHull(coordinates: List<GeoPoint>): List<GeoPoint> {
    val euclideanCoordinates = coordinates.map(GeoPoint::toEuclideanCoordinate)
    val leftmost = euclideanCoordinates.minBy { it.x }
    val hull = mutableListOf(leftmost)
    while(hull.last() != leftmost || hull.size == 1) {
        var next = euclideanCoordinates.first()
        euclideanCoordinates.forEach {
            if(hull.last() == next || checkClockWiseAngle(hull.last(), next, it) == 1) {
                next = it
            }
        }
        hull += next
    }
    return hull.map(EuclideanCoordinates::toGeoPoint)
}

/**
 * Function to check the ange direction given three points by the difference between the tow segment
 * slopes.
 *
 * @param a First coordinate.
 * @param b Second coordinate, which is the vertex of the angle.
 * @param c Third coordinate.
 *
 * @return Returns:
 * - `-1` if the angle formed is clockwise.
 * - `0` if the points are collinear.
 * - `1` if the angle formed is counterclockwise.
 */
private fun checkClockWiseAngle(
    a: EuclideanCoordinates,
    b: EuclideanCoordinates,
    c: EuclideanCoordinates
): Int {
    val product = (b.x - a.x) * (c.y - b.y) - (b.y - a.y) * (c.x - b.x)
    if (product == 0.0) return 0
    return if (product > 0) 1 else -1
}

/**
 * Function to converts a [GeoPoint] to its corresponding Euclidean coordinates.
 *
 * Extension function computes the Euclidean coordinates (x, y, z) from the geodesic coordinates
 * (latitude and longitude) using spherical to Cartesian conversion.
 *
 * @return An instance of [EuclideanCoordinates] representing the corresponding x, y, and z
 * coordinates in a Euclidean space.
 */
internal fun GeoPoint.toEuclideanCoordinate() =
    EuclideanCoordinates(
        x = 6371 * cos(this.latitudeAngle.inRadians()) * cos(this.longitudeAngle.inRadians()),
        y = 6371 * cos(this.latitudeAngle.inRadians()) * sin(this.longitudeAngle.inRadians()),
        z = 6371 * sin(this.latitudeAngle.inRadians())
    )