package apero.quanta.picai.data.remote.dto.genimg

import com.google.gson.annotations.SerializedName

data class GenerationInputRequest(
    @SerializedName("file")
    val file: String? = null,

    @SerializedName("files")
    val files: List<String>? = null,

    @SerializedName("prompts")
    val prompts: String? = null,
)