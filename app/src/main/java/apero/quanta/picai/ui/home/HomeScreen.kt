package apero.quanta.picai.ui.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import apero.quanta.picai.ui.components.ImagePicker
import apero.quanta.picai.ui.components.StyleCategoriesList

import apero.quanta.picai.ui.components.CustomSnackbarVisuals

@Composable
fun HomeRoute(
    snackbarHostState: SnackbarHostState,
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val state: HomeState by viewModel.viewState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.processIntent(HomeIntent.ImageSelected(uri))
        }
    )

    LaunchedEffect(Unit) {
        viewModel.homeEvent.collect { event ->
            when (event) {
                HomeEvent.OpenImagePicker -> {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }

                is HomeEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is HomeEvent.ShowSnackBar -> {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        CustomSnackbarVisuals(
                            message = event.message,
                            actionLabel = event.actionName,
                            duration = SnackbarDuration.Short,
                            containerColor = event.color
                        )
                    )
                    when (snackbarResult) {
                        SnackbarResult.Dismissed -> {}
                        SnackbarResult.ActionPerformed -> {}
                    }
                }
            }
        }
    }

    Box(modifier = modifier) {
        HomeScreen(
            state = state,
            onIntent = viewModel::processIntent,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        )
    }
}

@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageModel = state.generatedImageResult?.filePath ?: state.generatedImageResult?.urlImage
    ?: state.selectedImageUri

    Column(
        modifier = modifier
    ) {
        ImagePicker(
            model = imageModel,
            onClick = {
                onIntent(HomeIntent.PickImageClick)
            },
            removeImageOnClick = {
                onIntent(HomeIntent.ImageSelected(null))
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(6f)
                .padding(10.dp)
        )


        StyleCategoriesList(
            styleCategories = state.categories,
            selectedCategoryIndex = state.selectedCategoryIndex,
            selectedStyle = state.selectedStyle,
            onCategorySelected = {
                onIntent(HomeIntent.SelectCategory(it))
            },
            onStyleClick = {
                onIntent(HomeIntent.SelectStyle(it))
            },
            isLoading = state.isLoading,
            modifier = Modifier
                .weight(3f)
        )


        Button(
            onClick = {
                if (state.genSuccess) {
                    onIntent(HomeIntent.DownloadImageClick)
                } else {
                    onIntent(HomeIntent.GenImageClick)
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
           if(state.genSuccess){
               Text(
                   text = "Download Image",
                   style = MaterialTheme.typography.bodyLarge,
                   color = Color.White,
                   modifier = Modifier.padding(vertical = 8.dp)
               )
           }else{
               Text(
                   text = "Generate",
                   style = MaterialTheme.typography.bodyLarge,
                   color = Color.White,
                   modifier = Modifier.padding(vertical = 8.dp)
               )
           }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        Scaffold { innerPadding ->
            HomeScreen(
                state = HomeState(),
                onIntent = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}