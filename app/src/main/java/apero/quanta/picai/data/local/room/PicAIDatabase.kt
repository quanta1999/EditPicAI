package apero.quanta.picai.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import apero.quanta.picai.data.local.room.dao.HistoryDao
import apero.quanta.picai.data.local.room.entity.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class PicAIDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}