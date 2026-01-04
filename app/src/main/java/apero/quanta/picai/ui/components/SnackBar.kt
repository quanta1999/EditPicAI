package apero.quanta.picai.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomSnackbar(
    snackbarData: SnackbarData,
    containerColor: Color = Color.Red,
    contentColor: Color = Color.White,
    actionColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier,
        snackbarData = snackbarData,
        containerColor = Color.Red,
        contentColor = Color.White,
        actionColor = Color.White,
        dismissActionContentColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}
