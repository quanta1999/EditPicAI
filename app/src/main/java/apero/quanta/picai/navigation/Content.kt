package apero.quanta.picai.navigation

/**
 * Created by QuanTA on 05/01/2026.
 */

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import apero.quanta.picai.domain.model.History
import apero.quanta.picai.ui.imageview.ImageViewScreenRoute
import apero.quanta.picai.ui.history.HistoryRoute as HistoryRouteScreen
import apero.quanta.picai.ui.home.HomeRoute as HomeRouteScreen

fun EntryProviderScope<NavKey>.featureASection(
    snackbarHostState: SnackbarHostState,
) {
    entry<HomeRoute> {
        HomeRouteScreen(
            snackbarHostState = snackbarHostState,
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun EntryProviderScope<NavKey>.featureBSection(
    snackbarHostState: SnackbarHostState,
    onImageSelected: (History) -> Unit,
    onBack: () -> Unit
) {
    entry<HistoryRoute> {
        HistoryRouteScreen(
            snackbarHostState = snackbarHostState,
            onClickImage = { history ->
                onImageSelected(history)
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    entry<ImageViewRoute> { route ->
        ImageViewScreenRoute(
            history = route.history,
            onBack = onBack,
            snackbarHostState = snackbarHostState,
            modifier = Modifier.fillMaxSize()
        )
    }
}
