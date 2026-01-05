package apero.quanta.picai.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imagePath: String,
    val imageUrl: String? = null,
    val styleId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)