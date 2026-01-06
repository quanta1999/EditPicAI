package apero.quanta.picai.domain.usecase

import apero.quanta.picai.domain.repository.HistoryRepository
import jakarta.inject.Inject

/**
 * Created by QuanTA on 06/01/2026.
 */

class GetAllHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
     operator fun invoke() = historyRepository.getHistory()
}