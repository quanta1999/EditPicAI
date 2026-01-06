package apero.quanta.picai.navigation

/**
 * Created by QuanTA on 05/01/2026.
 */
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import apero.quanta.picai.ui.home.HomeRoute as HomeRouteScreen

fun EntryProviderScope<NavKey>.featureASection(
    snackbarHostState: SnackbarHostState
) {
    entry<HomeRoute> {
        HomeRouteScreen(
            snackbarHostState = snackbarHostState,
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun EntryProviderScope<NavKey>.featureBSection(
) {
    entry<HistoryRoute> {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "History",
                textAlign = TextAlign.Center
            )
        }
    }
}
