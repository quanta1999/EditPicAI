package apero.quanta.picai.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apero.quanta.picai.R
import apero.quanta.picai.domain.model.History
import coil.compose.AsyncImage
import java.io.File

import apero.quanta.picai.ui.components.CustomSnackbarVisuals

@Composable
fun HistoryRoute(
    snackbarHostState: SnackbarHostState,
    viewModel: HistoryViewModel = hiltViewModel(),
    onClickImage: (History) -> Unit,
    modifier: Modifier
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.historyEvent.collect { event ->
            when (event) {
                is HistoryEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        CustomSnackbarVisuals(
                            message = event.message,
                            containerColor = event.color
                        )
                    )
                }
            }
        }
    }

    HistoryScreen(
        state = state,
        onClickImage = onClickImage,
        modifier = modifier
    )
}

@Composable
fun HistoryScreen(
    state: HistoryState,
    onClickImage: (History) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.histories.isEmpty()) {
            Text(
                text = "No history yet",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.histories, key = { it.id }) { history ->
                    HistoryItem(
                        history = history,
                        onClickImage = { onClickImage(history) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    history: History,
    onClickImage: (History) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .clickable{
                onClickImage(history)
            }
    ){
        AsyncImage(
            model = File(history.imagePath),
            contentDescription = "History Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .size(200.dp)
        )
    }
}