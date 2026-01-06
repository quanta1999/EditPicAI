package apero.quanta.picai.ui.imageview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import apero.quanta.picai.R
import coil.compose.AsyncImage
import java.io.File

/**
 * Created by QuanTA on 06/01/2026.
 */

import apero.quanta.picai.domain.model.History

@Composable
fun ImageViewScreenRoute(
    history: History,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    ImageViewScreen(
        uri = history.imagePath,
        modifier = modifier
    )
}

@Composable
private fun ImageViewScreen(
    uri: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        AsyncImage(
            model = File(uri),
            error = painterResource(R.drawable.ic_launcher_background),
            placeholder = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "History Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ImageViewScreenPrev() {
    ImageViewScreen(
        uri = ""
    )
}