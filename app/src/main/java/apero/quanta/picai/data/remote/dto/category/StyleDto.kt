package apero.quanta.picai.data.remote.dto.category

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class StyleDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("documentId")
    val documentId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("isActive")
    val isActive: Boolean?,

    @SerializedName("style")
    val style: String?,

    @SerializedName("validation")
    val validation: JsonObject?,

    @SerializedName("thumbnails")
    val thumbnails: List<ThumbnailDto>?,
)