package apero.quanta.picai.ui.imageview

import apero.quanta.picai.domain.model.History
import androidx.compose.ui.graphics.Color


/**
 * Created by QuanTA on 06/01/2026.
 */

data class ImageViewState (
    val history: History? = null,
    val message: String = String(),
    val loading: Boolean = false,
    val error: String? = null
)

sealed class ImageViewIntent{
    data class LoadData(val history: History): ImageViewIntent()
    data class OnClickDelete(val history: History): ImageViewIntent()
}


sealed class ImageViewEvent{
    data class ShowSnackBar(val message: String, val color: Color = Color.Red): ImageViewEvent()
    data object OnClickBack: ImageViewEvent()
}