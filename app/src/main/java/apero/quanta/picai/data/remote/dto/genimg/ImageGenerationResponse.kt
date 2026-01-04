package apero.quanta.picai.data.remote.dto.genimg

import com.google.gson.annotations.SerializedName

data class GeneratedImageData(
    @SerializedName("url")
    val url: String,

    @SerializedName("path")
    val path: String,
)