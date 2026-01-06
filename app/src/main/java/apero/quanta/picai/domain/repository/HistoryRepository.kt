package apero.quanta.picai.domain.repository

import apero.quanta.picai.domain.model.History
import kotlinx.coroutines.flow.Flow

/**
 * Created by QuanTA on 06/01/2026.
 */

interface HistoryRepository {
    fun getHistory(): Flow<List<History>>
    suspend fun addHistory(history: History)
    suspend fun deleteHistory(history: History)
}