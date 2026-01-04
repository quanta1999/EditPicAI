package apero.quanta.picai.data.remote.dto.category

import com.google.gson.annotations.SerializedName

data class CategoryListResponse(
    @SerializedName("data")
    val data: List<CategoryDto>,

    @SerializedName("meta")
    val meta: MetaWrapper? = null,
)


data class StyleListResponse(
    @SerializedName("data")
    val data: List<TemplateCategoryDto>,

    @SerializedName("meta")
    val meta: MetaWrapper? = null,
)



// ==============================================================================

data class PaginationMeta(
    @SerializedName("page")
    val page: Int,

    @SerializedName("pageSize")
    val pageSize: Int,

    @SerializedName("pageCount")
    val pageCount: Int,

    @SerializedName("total")
    val total: Int,
)

/**
 * Meta wrapper containing pagination info
 *
 * @param pagination Pagination metadata
 */
data class MetaWrapper(
    @SerializedName("pagination")
    val pagination: PaginationMeta,
)