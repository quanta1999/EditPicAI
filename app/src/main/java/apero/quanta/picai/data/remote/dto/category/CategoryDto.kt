package apero.quanta.picai.data.remote.dto.category

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("documentId")
    val documentId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("order")
    val order: Int?,

    @SerializedName("aiType")
    val aiType: String?,

    @SerializedName("display")
    val display: List<String>?,

    @SerializedName("templateCategories")
    val templateCategories: List<TemplateCategoryDto>? = null,

    @SerializedName("styles")
    val styles: List<String>? = null,
)