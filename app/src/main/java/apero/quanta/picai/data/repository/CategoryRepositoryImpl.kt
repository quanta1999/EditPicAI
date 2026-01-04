package apero.quanta.picai.data.repository

import apero.quanta.picai.data.remote.service.CategoryDatasource
import apero.quanta.picai.domain.model.Category
import apero.quanta.picai.domain.model.ImageTemplate
import apero.quanta.picai.domain.repository.CategoryRepository
import com.google.android.gms.common.util.CollectionUtils.isEmpty
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDatasource: CategoryDatasource
) : CategoryRepository {
    override suspend fun getCategories(aiType: String): Result<List<Category>> {
        return categoryDatasource.getCategoriesWithStyles(aiType).map { resultData ->
            resultData.categories.filterNot { isEmpty(it.styles) }.map { categoryDto ->
                val styles = resultData.stylesByCategory[categoryDto.documentId]?.map { styleDto ->
                    val imageUrl = styleDto.thumbnails?.firstOrNull()?.url ?: styleDto.name
                    val styleName = styleDto.style?.firstOrNull()?.toString()
                    ImageTemplate(
                        id = styleDto.documentId,
                        name = styleName,
                        description = styleDto.description,
                        url = imageUrl
                    )
                } ?: emptyList()

                Category(
                    title = categoryDto.name,
                    styles = styles
                )
            }
        }
    }
}
