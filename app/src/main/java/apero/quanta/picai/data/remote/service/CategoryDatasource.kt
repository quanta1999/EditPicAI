package apero.quanta.picai.data.remote.service

import apero.quanta.picai.data.remote.dto.category.CategoryDto
import apero.quanta.picai.data.remote.dto.category.CategoryListResponse
import apero.quanta.picai.data.remote.dto.category.StyleDto

class CategoryDatasource(
    private val aiGenerationApiService: AiGenerationApiService,
) {
    suspend fun getCategoriesWithStyles(
        aiType: String? = null,
        display: String? = null,
    ): Result<CategoriesWithStylesResult>{
        return runCatching {
            val categoriesResult = fetchCategories(aiType, display)

            categoriesResult.fold(
                onSuccess = { categoryListResponse ->
                    val categories = categoryListResponse.data

                    if (categories.isEmpty()) {
                        return@runCatching createEmptyResult()
                    }

                    val stylesResult = extractStylesFromCategories(categories)

                    CategoriesWithStylesResult(
                        categories = categories,
                        stylesByCategory = stylesResult,
                    )
                },
                onFailure = { error ->
                    throw error
                },
            )
        }
    }

    private suspend fun fetchCategories(
        aiType: String? = null,
        display: String? = null
    ): Result<CategoryListResponse> {
        return aiGenerationApiService.getCategories(
            page = 1,
            limit = 100,
            aiType = aiType,
            display = display
        )
    }

    private fun createEmptyResult(): CategoriesWithStylesResult {
        return CategoriesWithStylesResult(
            categories = emptyList(),
            stylesByCategory = emptyMap(),
        )
    }
}

private fun extractStylesFromCategories(
    categories: List<CategoryDto>
): Map<String, List<StyleDto>> {
    return categories.associate { category ->
        // Extract StyleDto from each TemplateCategoryDto
        // Map to non-null templates and filter out nulls
        val styles = category.templateCategories
            ?.mapNotNull { it.template }
            ?: emptyList()

        category.documentId to styles
    }
}



data class CategoriesWithStylesResult(
    val categories: List<CategoryDto>,
    val stylesByCategory: Map<String, List<StyleDto>>,
)