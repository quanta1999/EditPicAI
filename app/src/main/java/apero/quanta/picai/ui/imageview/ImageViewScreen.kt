package apero.quanta.picai.ui.imageview

/**
 * Created by QuanTA on 06/01/2026.
 */

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apero.quanta.picai.R
import apero.quanta.picai.domain.model.History
import apero.quanta.picai.ui.components.CustomSnackbarVisuals
import apero.quanta.picai.ui.theme.PicAITheme
import apero.quanta.picai.util.shareImage
import coil.compose.AsyncImage
import java.io.File

@Composable
fun ImageViewScreenRoute(
    history: History,
    onBack: () -> Unit,
    viewModel: ImageViewViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val state: ImageViewState by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

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
            initialHistory = state.history!!,
            histories = state.histories,
            onIntent = viewModel::processIntent,
            context = context,
            modifier = modifier
        )
    }
}

@Composable
private fun ImageViewScreen(
    initialHistory: History,
    histories: List<History>,
    onIntent: (ImageViewIntent) -> Unit,
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { histories.size })
    var hasScrolledToInitial by remember { mutableStateOf(false) }

    LaunchedEffect(histories) {
        if (!hasScrolledToInitial && histories.isNotEmpty()) {
            val index = histories.indexOfFirst { it.id == initialHistory.id }
            if (index != -1) {
                pagerState.scrollToPage(index)
            }
            hasScrolledToInitial = true
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(9f)
        ) { page ->
            if (page < histories.size) {
                val history = histories[page]
                AsyncImage(
                    model = File(history.imagePath),
                    error = painterResource(R.drawable.image_not_found),
                    placeholder = painterResource(R.drawable.image_not_found),
                    contentDescription = "History Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            modifier = Modifier.background(Color.White)
        ) {
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    if (histories.isNotEmpty() && pagerState.currentPage < histories.size) {
                        onIntent(ImageViewIntent.OnClickDelete(histories[pagerState.currentPage]))
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = "Delete"
                )
            }

            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    shareImage(
                        histories.getOrNull(pagerState.currentPage)?.imagePath.toString(),
                        context
                    )
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = "Share"
                )
            }
        }
    }
}

@Preview
@Composable
private fun ImageViewScreenPrev() {
    PicAITheme {
        ImageViewScreen(
            initialHistory = History(
                id = 0,
                imagePath = "",
                imageUrl = "",
                styleId = ""
            ),
            histories = listOf(
                History(
                    id = 0,
                    imagePath = "",
                    imageUrl = "",
                    styleId = ""
                )
            ),
            onIntent = {},
            modifier = Modifier
        )
    }
}