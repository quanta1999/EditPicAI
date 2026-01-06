package apero.quanta.picai.ui.imageview

/**
 * Created by QuanTA on 06/01/2026.
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apero.quanta.picai.R
import apero.quanta.picai.domain.model.History
import apero.quanta.picai.ui.theme.PicAITheme
import coil.compose.AsyncImage
import java.io.File

import apero.quanta.picai.ui.components.CustomSnackbarVisuals

@Composable
fun ImageViewScreenRoute(
    history: History,
    onBack: () -> Unit,
    viewModel: ImageViewViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val state: ImageViewState by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(history) {
        viewModel.processIntent(ImageViewIntent.LoadData(history))
    }

    LaunchedEffect(Unit) {
        viewModel.imageViewEvent.collect { event ->
            when (event) {
                is ImageViewEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        CustomSnackbarVisuals(
                            message = event.message,
                            containerColor = event.color
                        )
                    )
                }

                is ImageViewEvent.OnClickBack -> {
                    onBack()
                }
            }
        }
    }

    if (state.history != null) {
        ImageViewScreen(
            history = state.history!!,
            onIntent = viewModel::processIntent,
            modifier = modifier
        )
    }
}

@Composable
private fun ImageViewScreen(
    history: History,
    onIntent: (ImageViewIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = File(history.imagePath),
            error = painterResource(R.drawable.ic_launcher_background),
            placeholder = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "History Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .weight(9f)
        )

        Row(
            modifier = Modifier.background(Color.White)
        ) {
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onIntent(ImageViewIntent.OnClickDelete(history))
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete"
                )
            }
        }
    }
}

@Preview
@Composable
private fun ImageViewScreenPrev() {
    PicAITheme{
        ImageViewScreen(
            history = History(
                id = 0,
                imagePath = "",
                imageUrl = "",
                styleId = ""
            ),
            onIntent = {},
            modifier = Modifier
        )
    }
}