package apero.quanta.picai.data.remote.dto.genimg

import com.google.gson.annotations.SerializedName

data class PresignedUrlData(
    @SerializedName("presignedUrl")
    val presignedUrl: String,

    @SerializedName("objectKey")
    val objectKey: String,
)