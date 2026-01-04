package apero.quanta.picai.data.remote.dto.category

import com.google.gson.annotations.SerializedName

data class TemplateCategoryDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("documentId")
    val documentId: String,

    @SerializedName("order")
    val order: Int?,

    @SerializedName("template")
    val template: StyleDto?,
)