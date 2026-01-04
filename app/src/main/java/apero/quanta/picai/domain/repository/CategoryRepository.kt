package apero.quanta.picai.domain.repository

import apero.quanta.picai.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(aiType: String = "image2image"): Result<List<Category>>
}
