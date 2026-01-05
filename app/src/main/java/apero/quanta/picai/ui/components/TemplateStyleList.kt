package apero.quanta.picai.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import apero.quanta.picai.domain.model.Style
import coil.compose.AsyncImage

import androidx.compose.foundation.border

@Composable
fun TemplateStyleList(
    styles: List<Style>,
    selectedStyle: Style?,
    onStyleSelected: (Style) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(16.dp),
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
    ) {
        items(styles.size) { it ->
            TemplateStyleItem(
                style = styles[it],
                isSelected = styles[it].id == selectedStyle?.id,
                onStyleClick = { onStyleSelected(styles[it]) },
            )
        }
    }
}

@Composable
fun TemplateStyleItem(
    style: Style,
    isSelected: Boolean,
    onStyleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderModifier = if (isSelected) {
        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .width(56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                onClick = onStyleClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = style.url,
            contentDescription = style.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .then(borderModifier)
                .size(56.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = style.name?:"QuanTA",
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}
