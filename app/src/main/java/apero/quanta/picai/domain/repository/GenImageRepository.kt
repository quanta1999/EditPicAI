package apero.quanta.picai.domain.repository

import apero.quanta.picai.domain.model.genimg.ImageResult
import apero.quanta.picai.domain.model.genimg.InputGeneration

interface GenImageRepository {
    suspend fun genImage(
        inputGenImg: InputGeneration.ImageInputGeneration,
    ): Result<ImageResult>
}