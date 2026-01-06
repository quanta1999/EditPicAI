package apero.quanta.picai.domain.usecase

import apero.quanta.picai.domain.model.History
import apero.quanta.picai.domain.repository.HistoryRepository
import jakarta.inject.Inject

/**
 * Created by QuanTA on 06/01/2026.
 */

class DeleteHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(history: History) = historyRepository.deleteHistory(history)
}