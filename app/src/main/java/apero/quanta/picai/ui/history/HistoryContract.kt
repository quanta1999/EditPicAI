package apero.quanta.picai.ui.history

import apero.quanta.picai.domain.model.History

data class HistoryState(
    val isLoading: Boolean = false,
    val histories: List<History> = emptyList(),
    val error: String? = null
)

sealed class HistoryIntent {
    data object LoadHistory : HistoryIntent()
    data class DeleteHistory(val history: History) : HistoryIntent()
    data class OnImageClick(val imagePath: String) : HistoryIntent()

}

sealed class HistoryEvent {
    data class ShowSnackBar(val message: String) : HistoryEvent()
}