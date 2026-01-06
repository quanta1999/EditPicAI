package apero.quanta.picai.ui.imageview

/**
 * Created by QuanTA on 06/01/2026.
 */

data class ImageViewState (
    val loading: Boolean = false,
    val error: String? = null
)

sealed class ImageViewIntent{
    data object OnClickDelete: ImageViewIntent()
}

sealed class ImageViewEvent{
    data class ShowSnackBar(val message: String): ImageViewEvent()
}