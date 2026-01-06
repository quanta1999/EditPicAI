package apero.quanta.picai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import apero.quanta.picai.navigation.HomeRoute
import apero.quanta.picai.navigation.ImageViewRoute
import apero.quanta.picai.navigation.NavigationState
import apero.quanta.picai.navigation.Navigator
import apero.quanta.picai.navigation.TOP_LEVEL_ROUTES
import apero.quanta.picai.navigation.featureASection
import apero.quanta.picai.navigation.featureBSection
import apero.quanta.picai.navigation.rememberNavigationState
import apero.quanta.picai.navigation.toEntries
import apero.quanta.picai.ui.theme.PicAITheme
import apero.quanta.picai.ui.theme.setEdgeToEdgeConfig
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setEdgeToEdgeConfig()
        super.onCreate(savedInstanceState)
        setContent {
            PicAITheme {
                val navigationState: NavigationState = rememberNavigationState(
                    startRoute = HomeRoute,
                    topLevelRoutes = TOP_LEVEL_ROUTES.keys
                )
                val snackbarHostState = remember { SnackbarHostState() }
                val navigator = remember { Navigator(navigationState) }

                val entryProvider = entryProvider {
                    featureASection(
                        snackbarHostState = snackbarHostState
                    )
                    featureBSection(
                        snackbarHostState = snackbarHostState,
                        onImageSelected = { history ->
                            navigator.navigate(ImageViewRoute(history))
                        }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            TOP_LEVEL_ROUTES.forEach { (key, value) ->
                                val isSelected = key == navigationState.topLevelRoute
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { navigator.navigate(key) },
                                    icon = {
                                        Icon(
                                            painter = painterResource(value.icon),
                                            contentDescription = value.description
                                        )
                                    },
                                    label = { Text(value.description) }
                                )
                            }
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { paddingValues ->
                    NavDisplay(
                        entries = navigationState.toEntries(entryProvider),
                        onBack = { navigator.goBack() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}