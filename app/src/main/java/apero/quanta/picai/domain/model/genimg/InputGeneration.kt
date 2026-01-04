package apero.quanta.picai.domain.model.genimg

import android.os.Parcelable
import apero.quanta.picai.domain.model.TemplateModel
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface InputGeneration : Parcelable {
    val style: TemplateModel
    val files: List<String>
    @Parcelize
    data class ImageInputGeneration(
        override val style: TemplateModel,
        override val files: List<String>,
    ) : InputGeneration
}