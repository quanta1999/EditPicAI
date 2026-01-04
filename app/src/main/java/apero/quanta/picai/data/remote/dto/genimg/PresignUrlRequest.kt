package apero.quanta.picai.data.remote.dto.genimg

import com.google.gson.annotations.SerializedName
import java.io.File

data class PresignedUrlRequest(
    @SerializedName("fileName")
    val fileName: String,

    @SerializedName("contentType")
    val contentType: String,

    @SerializedName("expiresIn")
    val expiresIn: Int = 3600, // Default 1 hour
)