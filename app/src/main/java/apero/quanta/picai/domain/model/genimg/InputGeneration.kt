package apero.quanta.picai.domain.model.genimg

import android.os.Parcelable
import apero.quanta.picai.domain.model.Style
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface InputGeneration : Parcelable {
    val style: Style
    val files: List<String>
    @Parcelize
    data class ImageInputGeneration(
        override val style: Style,
        override val files: List<String>,
    ) : InputGeneration
}