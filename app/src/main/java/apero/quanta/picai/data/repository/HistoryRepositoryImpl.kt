package apero.quanta.picai.data.repository

import apero.quanta.picai.data.local.room.dao.HistoryDao
import apero.quanta.picai.data.local.room.entity.toDomain
import apero.quanta.picai.domain.model.History
import apero.quanta.picai.domain.model.toEntity
import apero.quanta.picai.domain.repository.HistoryRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by QuanTA on 06/01/2026.
 */

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {
    override fun getHistory(): Flow<List<History>> {
        return historyDao.getAllHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addHistory(history: History) {
        return historyDao.insertHistory(history.toEntity())
    }

    override suspend fun deleteHistory(history: History) {
        return historyDao.deleteHistory(history.toEntity())
    }

}