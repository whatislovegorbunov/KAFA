package com.example.kafa.presentation.navigation

sealed interface Route {
    val route: String

    data object Home : Route {
        override val route = "home"
    }

    data object AddNote : Route {
        override val route = "add_note"
    }
}
