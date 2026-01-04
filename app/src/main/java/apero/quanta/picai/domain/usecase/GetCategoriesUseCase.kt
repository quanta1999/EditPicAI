package apero.quanta.picai.domain.usecase

import apero.quanta.picai.domain.model.Category
import apero.quanta.picai.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(aiType: String = "image2image"): Result<List<Category>> {
        return categoryRepository.getCategories(aiType)
    }
}
