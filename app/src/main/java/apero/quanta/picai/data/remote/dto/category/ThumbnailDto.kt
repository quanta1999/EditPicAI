package apero.quanta.picai.data.remote.dto.category

import com.google.gson.annotations.SerializedName

data class ThumbnailDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("documentId")
    val documentId: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("alternativeText")
    val alternativeText: String?,

    @SerializedName("caption")
    val caption: String?,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("formats")
    val formats: ThumbnailFormatsDto?,

    @SerializedName("hash")
    val hash: String,

    @SerializedName("ext")
    val ext: String,

    @SerializedName("mime")
    val mime: String,

    @SerializedName("size")
    val size: Double,

    @SerializedName("url")
    val url: String,

    @SerializedName("previewUrl")
    val previewUrl: String?,

    @SerializedName("provider")
    val provider: String,

    @SerializedName("provider_metadata")
    val provider_metadata: Any?,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("publishedAt")
    val publishedAt: String,

    @SerializedName("isUrlSigned")
    val isUrlSigned: Boolean,
)


data class ThumbnailFormatDto(
    @SerializedName("ext")
    val ext: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("hash")
    val hash: String,

    @SerializedName("mime")
    val mime: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("path")
    val path: String?,

    @SerializedName("size")
    val size: Double,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("sizeInBytes")
    val sizeInBytes: Int,

    @SerializedName("isUrlSigned")
    val isUrlSigned: Boolean,
)


data class ThumbnailFormatsDto(
    @SerializedName("thumbnail")
    val thumbnail: ThumbnailFormatDto?,

    @SerializedName("small")
    val small: ThumbnailFormatDto?,

    @SerializedName("medium")
    val medium: ThumbnailFormatDto?,

    @SerializedName("large")
    val large: ThumbnailFormatDto?,
)