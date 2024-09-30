package com.tomtom.maps.exampleapp


sealed class Screen(val route: String) {
    data object Home : Screen(
        route = "home"
    )

    data object  Map : Screen(
        route = "map"
    )
}
