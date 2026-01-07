package apero.quanta.picai.ui.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apero.quanta.picai.domain.model.History
import apero.quanta.picai.domain.model.genimg.InputGeneration
import apero.quanta.picai.domain.usecase.GenImageUseCase
import apero.quanta.picai.domain.usecase.GetCategoriesUseCase
import apero.quanta.picai.domain.usecase.InsertHistoryUseCase
import apero.quanta.picai.util.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val genImageUseCase: GenImageUseCase,
    private val insertHistoryUseCase: InsertHistoryUseCase,
    private val fileUtils: FileUtils
) : ViewModel() {

    private val _viewState = MutableStateFlow(HomeState())
    val viewState: StateFlow<HomeState> = _viewState.asStateFlow()

    private val _homeEvent = Channel<HomeEvent>(Channel.BUFFERED)
    val homeEvent = _homeEvent.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        fetchCategory()
    }

    fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadData -> {
                fetchCategory()
            }

            HomeIntent.GenImageClick -> {
                genImage()
            }

            HomeIntent.RetryLoadClick -> {
                loadData()
            }

            is HomeIntent.SelectCategory -> {
                _viewState.update { it.copy(selectedCategoryIndex = intent.index) }
            }

            is HomeIntent.SelectStyle -> {
                _viewState.update {
                    it.copy(
                        selectedStyle = intent.style,
                        genSuccess = false,
                        generatedImageResult = null
                    )
                }
            }

            is HomeIntent.ImageSelected -> {
                _viewState.update {
                    it.copy(
                        selectedImageUri = intent.uri,
                        generatedImageResult = null,
                        genSuccess = false
                    )
                }
            }

            HomeIntent.PickImageClick -> {
                viewModelScope.launch {
                    _homeEvent.send(HomeEvent.OpenImagePicker)
                }
            }

            HomeIntent.DownloadImageClick -> {
                downloadImage()
            }
        }
    }


    private fun fetchCategory() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true) }
            val result = getCategoriesUseCase()
            result.fold(
                onSuccess = { categories ->
                    _viewState.update {
                        it.copy(
                            isLoading = false,
                            categories = categories
                        )
                    }
                },
                onFailure = { error ->
                    _viewState.update { it.copy(isLoading = false, error = error.message) }
                    _homeEvent.send(HomeEvent.ShowSnackBar("Error: ${error.message}"))
                }
            )
        }
    }

    private fun genImage() {
        val currentState = _viewState.value
        val selectedStyle = currentState.selectedStyle
        val selectedUri = currentState.selectedImageUri

        if (selectedStyle == null) {
            viewModelScope.launch { _homeEvent.send(HomeEvent.ShowToast("Please select a style")) }
            return
        }

        if (selectedUri == null) {
            viewModelScope.launch { _homeEvent.send(HomeEvent.ShowSnackBar("Please select an image")) }
            return
        }

        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true) }
            try {
                val file = fileUtils.uriToFile(selectedUri)
                if (file != null) {
                    val inputGen = InputGeneration.ImageInputGeneration(
                        style = selectedStyle,
                        files = listOf(file.absolutePath)
                    )

                    // Call use case
                    val result = genImageUseCase.invoke(inputGen)
                    result.fold(
                        onSuccess = { imageResult ->
                            _viewState.update {
                                it.copy(
                                    genSuccess = true,
                                    isLoading = false,
                                    generatedImageResult = imageResult
                                )
                            }
                        },
                        onFailure = { e ->
                            _viewState.update { it.copy(isLoading = false, error = e.message) }
                            _homeEvent.send(HomeEvent.ShowSnackBar("Generation failed: ${e.message}"))
                        }
                    )
                } else {
                    _viewState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to process image"
                        )
                    }
                    _homeEvent.send(HomeEvent.ShowToast("Failed to process image"))
                }
            } catch (e: Exception) {
                _viewState.update { it.copy(isLoading = false, error = e.message) }
                _homeEvent.send(HomeEvent.ShowToast("Error: ${e.message}"))
            }
        }
    }

    private fun downloadImage() {
        viewModelScope.launch {
            val url = _viewState.value.generatedImageResult?.urlImage
            if (url != null) {
                _viewState.update { it.copy(isLoading = true) }
                val file = fileUtils.downloadImage(url)
                _viewState.update { it.copy(isLoading = false) }
                if (file != null) {
                    _homeEvent.send(HomeEvent.ShowSnackBar("Image download successfully", color = Color.Black))
                    insertHistoryUseCase(
                        history = History(
                            imagePath = file.absolutePath,
                            imageUrl = url,
                            styleId = _viewState.value.selectedStyle?.id,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                } else {
                    _homeEvent.send(HomeEvent.ShowToast("Failed to download image"))
                }
            } else {
                _homeEvent.send(HomeEvent.ShowSnackBar("No image URL found"))
            }
        }
    }
}
        