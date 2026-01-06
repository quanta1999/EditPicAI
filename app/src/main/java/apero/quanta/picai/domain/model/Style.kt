package apero.quanta.picai.domain.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class Style(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val url: String? = null,
) : Parcelable
