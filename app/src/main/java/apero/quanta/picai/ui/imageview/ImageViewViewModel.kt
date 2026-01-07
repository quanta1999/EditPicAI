package apero.quanta.picai.ui.imageview

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apero.quanta.picai.domain.model.History
import apero.quanta.picai.domain.usecase.DeleteHistoryUseCase
import apero.quanta.picai.domain.usecase.GetAllHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by QuanTA on 06/01/2026.
 */

@HiltViewModel
class ImageViewViewModel @Inject constructor(
    private val deleteImageUseCase: DeleteHistoryUseCase,
    private val getAllHistoryUseCase: GetAllHistoryUseCase
) : ViewModel() {
    private val _viewState = MutableStateFlow(ImageViewState())
    val viewState: StateFlow<ImageViewState> = _viewState.asStateFlow()

    private val _imageViewEvent = Channel<ImageViewEvent>(Channel.BUFFERED)
    val imageViewEvent = _imageViewEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            getAllHistoryUseCase().collect { histories ->
                _viewState.update { it.copy(histories = histories) }
            }
        }
    }


    fun processIntent(intent: ImageViewIntent) {
        when (intent) {
            is ImageViewIntent.LoadData -> {
                _viewState.update { it.copy(history = intent.history) }
            }
            is ImageViewIntent.OnClickDelete -> {
                deleteHistory(intent.history)
            }
        }
    }



    private fun deleteHistory(history: History) {
        viewModelScope.launch {
            _viewState.update { it.copy(loading = true) }
            deleteImageUseCase(history)
            val file = File(history.imagePath)
            if(file.exists()){
                val deleted = file.delete()
                if(!deleted){
                    _viewState.update { it.copy(error = "Error deleting image") }
                }
            }
            _viewState.update { it.copy(loading = false) }
            _imageViewEvent.send(ImageViewEvent.ShowSnackBar("Image deleted", Color.Red))
        }

    }
}