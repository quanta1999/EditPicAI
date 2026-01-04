package apero.quanta.picai.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemplateModel(
    val id: String,
    val name: String,
    val description: String? = null,
): Parcelable