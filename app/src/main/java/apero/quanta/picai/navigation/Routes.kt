package apero.quanta.picai.navigation

import androidx.navigation3.runtime.NavKey
import apero.quanta.picai.R
import apero.quanta.picai.domain.model.History
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Created by QuanTA on 05/01/2026.
 */

@Serializable
data object HomeRoute : NavKey

@Serializable
data object HistoryRoute : NavKey

@Serializable
data object SettingRoute : NavKey

@Serializable
data class ImageViewRoute(@Contextual val history : History) : NavKey
data class NavBarItem(
    val icon: Int,
    val description: String
)

val TOP_LEVEL_ROUTES = mapOf<NavKey, NavBarItem>(
    HomeRoute to NavBarItem(icon = R.drawable.ic_ai, description = "AI Beauty"),
    HistoryRoute to NavBarItem(icon = R.drawable.ic_history, description = "History"),
    SettingRoute to NavBarItem(icon = R.drawable.ic_settings, description = "Settings"),
)
