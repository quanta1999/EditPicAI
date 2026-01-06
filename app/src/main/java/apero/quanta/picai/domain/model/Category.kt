package apero.quanta.picai.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Category(
    val title: String,
    val styles: List<Style>,
)
