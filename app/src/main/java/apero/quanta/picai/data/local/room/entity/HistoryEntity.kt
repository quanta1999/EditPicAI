package apero.quanta.picai.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import apero.quanta.picai.domain.model.History

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imagePath: String,
    val imageUrl: String? = null,
    val styleId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// convert to domain model
fun HistoryEntity.toDomain() = History(
    id = id,
    imagePath = imagePath,
    imageUrl = imageUrl,
    styleId = styleId,
    createdAt = createdAt
)