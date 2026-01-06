package apero.quanta.picai.ui.home

import android.net.Uri
import apero.quanta.picai.domain.model.Category
import apero.quanta.picai.domain.model.Style

import apero.quanta.picai.domain.model.genimg.ImageResult
import androidx.compose.ui.graphics.Color


data class HomeState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val selectedCategoryIndex: Int = 0,
    val selectedStyle: Style? = null,
    val genSuccess: Boolean = false,
    val error: String? = null,
    val selectedImageUri: Uri? = null,
    val generatedImageResult: ImageResult? = null,
)

sealed class HomeIntent {
    data object LoadData : HomeIntent()
    data class SelectCategory(val index: Int) : HomeIntent()
    data class SelectStyle(val style: Style) : HomeIntent()
    data object RetryLoadClick : HomeIntent()
    data object GenImageClick : HomeIntent()
    data class ImageSelected(val uri: Uri?) : HomeIntent()
    data object PickImageClick : HomeIntent()
    data object DownloadImageClick : HomeIntent()
}


sealed class HomeEvent {
    data object OpenImagePicker : HomeEvent()
    data class ShowToast(val message: String) : HomeEvent()
    data class ShowSnackBar(
        val message: String,
        val actionName: String = "Close",
        val color: Color = Color.Red
    ) : HomeEvent()
}
