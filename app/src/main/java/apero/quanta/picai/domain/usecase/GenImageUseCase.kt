package apero.quanta.picai.domain.usecase

import apero.quanta.picai.domain.model.genimg.ImageResult
import apero.quanta.picai.domain.model.genimg.InputGeneration
import apero.quanta.picai.domain.repository.GenImageRepository
import jakarta.inject.Inject


class GenImageUseCase @Inject constructor(
    private val genImageRepository: GenImageRepository,
) {
    suspend fun invoke(inputGeneration: InputGeneration.ImageInputGeneration): Result<ImageResult> {
        return genImageRepository.genImage(inputGeneration)
    }
}