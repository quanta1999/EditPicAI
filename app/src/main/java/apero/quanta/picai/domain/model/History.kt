package apero.quanta.picai.domain.model

import apero.quanta.picai.data.local.room.entity.HistoryEntity

/**
 * Created by QuanTA on 06/01/2026.
 */

data class History (
    val id: Int = 0,
    val imagePath: String,
    val imageUrl: String? = null,
    val styleId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

// convert to entity model
fun History.toEntity() = HistoryEntity(
    id = id,
    imagePath = imagePath,
    imageUrl = imageUrl,
    styleId = styleId,
    createdAt = createdAt
)