package apero.quanta.picai.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class CustomSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    val containerColor: Color = Color.Red,
    val contentColor: Color = Color.White,
    val actionColor: Color = Color.White
) : SnackbarVisuals

@Composable
fun CustomSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    val visuals = snackbarData.visuals as? CustomSnackbarVisuals
    val containerColor = visuals?.containerColor ?: Color.Red
    val contentColor = visuals?.contentColor ?: Color.White
    val actionColor = visuals?.actionColor ?: Color.White

    Snackbar(
        modifier = modifier,
        snackbarData = snackbarData,
        containerColor = containerColor,
        contentColor = contentColor,
        actionColor = actionColor,
        dismissActionContentColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}
