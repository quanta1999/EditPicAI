package apero.quanta.picai.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun InputArea(
    value: String,
    onValueChange: (String) -> Unit,
    onClickReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Enter a value") },
        maxLines = 2,
        shape = TextFieldDefaults.shape,
        modifier = modifier
    )
}

@Preview
@Composable
private fun InputAreaPreview() {
    InputArea(
        value = "",
        onValueChange = {},
        onClickReset = {}
    )
}