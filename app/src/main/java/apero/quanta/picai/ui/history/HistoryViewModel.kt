package apero.quanta.picai.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apero.quanta.picai.domain.usecase.DeleteHistoryUseCase
import apero.quanta.picai.domain.usecase.GetAllHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllHistoryUseCase: GetAllHistoryUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(HistoryState())
    val viewState: StateFlow<HistoryState> = _viewState.asStateFlow()

    private val _historyEvent = Channel<HistoryEvent>(Channel.BUFFERED)
    val historyEvent = _historyEvent.receiveAsFlow()

    init {
        loadHistory()
    }

    fun processIntent(intent: HistoryIntent) {
        when (intent) {
            HistoryIntent.LoadHistory -> loadHistory()
            is HistoryIntent.DeleteHistory -> deleteHistory(intent)
            is HistoryIntent.OnImageClick -> {
                /* no-op */
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true) }
            getAllHistoryUseCase()
                .catch { e ->
                    _viewState.update { it.copy(isLoading = false, error = e.message) }
                    _historyEvent.send(HistoryEvent.ShowSnackBar("Error loading history: ${e.message}"))
                }
                .collect { histories ->
                    _viewState.update {
                        it.copy(
                            isLoading = false,
                            histories = histories
                        )
                    }
                }
        }
    }

    private fun deleteHistory(intent: HistoryIntent.DeleteHistory) {
        viewModelScope.launch {
            try {
                deleteHistoryUseCase(intent.history)
                _historyEvent.send(HistoryEvent.ShowSnackBar("Image deleted"))
            } catch (e: Exception) {
                _historyEvent.send(HistoryEvent.ShowSnackBar("Failed to delete: ${e.message}"))
            }
        }
    }
}
