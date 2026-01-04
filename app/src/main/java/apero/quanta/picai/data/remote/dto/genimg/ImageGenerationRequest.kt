package apero.quanta.picai.data.remote.dto.genimg

import com.google.gson.annotations.SerializedName

data class ImageGenerationRequest(
    @SerializedName("documentId")
    val documentId: String,

    @SerializedName("input")
    val input: GenerationInputRequest,
)