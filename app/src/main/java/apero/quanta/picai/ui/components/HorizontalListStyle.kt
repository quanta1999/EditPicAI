package apero.quanta.picai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import apero.quanta.picai.R
import apero.quanta.picai.domain.model.Category
import apero.quanta.picai.domain.model.Style
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleCategoriesList(
    styleCategories: List<Category>,
    selectedCategoryIndex: Int,
    selectedStyle: Style?,
    onCategorySelected: (Int) -> Unit,
    onStyleClick: (Style) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            LoadingView()
        }
        return
    }

    if (styleCategories.isEmpty()) return

    // Coroutine scope for animated page scrolling
    val coroutineScope = rememberCoroutineScope()

    // Pager state for smooth scrolling between category pages
    val pagerState = rememberPagerState(
        initialPage = selectedCategoryIndex.coerceIn(0, styleCategories.size - 1),
        pageCount = { styleCategories.size },
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                if (page != selectedCategoryIndex) {
                    onCategorySelected(page)
                }
            }
    }

    // Sync pager scroll to ViewModel (single direction: Pager → ViewModel)
    LaunchedEffect(selectedCategoryIndex) {
         if (pagerState.currentPage != selectedCategoryIndex) {
            pagerState.animateScrollToPage(selectedCategoryIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Section title
        Text(
            text = stringResource(R.string.choose_style),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
        )

        // Material Design 3 TabRow with gradient text and animated indicator
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = @Composable {
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(pagerState.currentPage, matchContentSize = true)
                        .height(1.dp)
                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                        .background(color = MaterialTheme.colorScheme.onBackground),
                )
            },
            divider = {},
            edgePadding = 8.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            styleCategories.forEachIndexed { index, category ->
                val isSelected = pagerState.currentPage == index
                val interactionSource = remember { MutableInteractionSource() }
                Tab(
                    selected = isSelected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .height(30.dp)
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(20))
                        .indication(
                            interactionSource = interactionSource,
                            indication = ripple(bounded = true)
                        ),
                ) {
                    // Text content - bold when selected, normal when not
                    Text(
                        text = category.title,
                        style = if (isSelected) {
                            MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        } else {
                            MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                            )
                        },
                    )
                }
            }
        }

        // HorizontalPager for smooth category switching
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { pageIndex ->
            TemplateStyleList(
                styles = styleCategories[pageIndex].styles,
                selectedStyle = selectedStyle,
                onStyleSelected = onStyleClick,
            )

        }
    }
}