package apero.quanta.picai.data.repository

import apero.quanta.picai.data.remote.dto.genimg.GenerationInputRequest
import apero.quanta.picai.data.remote.dto.genimg.ImageGenerationRequest
import apero.quanta.picai.data.remote.dto.genimg.PresignedUrlRequest
import apero.quanta.picai.data.remote.service.AiGenerationApiService
import apero.quanta.picai.domain.model.genimg.ImageResult
import apero.quanta.picai.domain.model.genimg.InputGeneration
import apero.quanta.picai.domain.repository.GenImageRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File

class GenImageRepositoryImpl @Inject constructor(
    private val aiGenerationApiService: AiGenerationApiService,
) : GenImageRepository {
    override suspend fun genImage(inputGenImg: InputGeneration.ImageInputGeneration): Result<ImageResult> =
        runCatching {
            withContext(Dispatchers.IO) {
                val inputFiles = inputGenImg.files.map { File(it.toString()) }
                require(inputFiles.isNotEmpty()) { "At least one input file is required" }

                val style = inputGenImg.style
                Timber.d("Starting AI generation for style: ${style.id} with ${inputFiles.size} file(s)")

                // Step 1 & 2: Get presigned URLs and upload all files
                val uploadedObjectKeys = inputFiles.map { file ->
                    uploadFileToS3(file)
                }

                Timber.d("All files uploaded to S3 successfully, object keys: $uploadedObjectKeys")

                // Step 3: Generate AI image

                val generationInputRequest =
                    GenerationInputRequest(file = uploadedObjectKeys.first())


                val generationRequest = ImageGenerationRequest(
                    documentId = style.id,
                    input = generationInputRequest,
                )

                val generationResponse = try {
                    aiGenerationApiService.generateImage(generationRequest).getOrThrow()
                } catch (e: Exception) {
                    throw e
                }

                val generatedImageUrl = generationResponse.url

                Timber.d("AI generation completed successfully, URL: $generatedImageUrl")

                // Step 4: Download the generated image to a temporary file
                val resultFile = downloadFile(generatedImageUrl)

                Timber.d("Generated image downloaded to: ${resultFile.absolutePath}")

                // Return both the URL and the downloaded file
                ImageResult(
                    filePath = resultFile.absolutePath,
                    urlImage = generatedImageUrl
                )
            }
        }


    private suspend fun downloadFile(url: String): File = withContext(Dispatchers.IO) {
        val ext = url.substringAfterLast('.', "jpg").substringBefore('?').takeIf { it.length <= 5 }
            ?: "jpg"
        val tempFile = File.createTempFile("generated_", ".$ext")
        try {
            java.net.URL(url).openStream().use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Timber.d("Downloaded file from $url to ${tempFile.absolutePath}")
            tempFile
        } catch (e: Exception) {
            Timber.e(e, "Failed to download file from $url")
            throw e
        }
    }


    private suspend fun uploadFileToS3(file: File): String {
        // Get presigned URL for upload
        val presignedUrlRequest = PresignedUrlRequest(
            fileName = file.name,
            contentType = getContentType(file),
            expiresIn = 3600,
        )

        val presignedUrlResponse = try {
            aiGenerationApiService.getPresignedUrl(presignedUrlRequest).getOrThrow()
        } catch (e: Exception) {
            throw e
        }

        Timber.d("Got presigned URL for ${file.name}, objectKey: ${presignedUrlResponse.objectKey}")

        // Upload file to S3
        val requestBody = file.asRequestBody("image/*".toMediaType())

        try {
            aiGenerationApiService.uploadToS3(
                url = presignedUrlResponse.presignedUrl,
                body = requestBody,
            ).getOrThrow()
        } catch (e: Exception) {
            throw e
        }

        Timber.d("File ${file.name} uploaded to S3 successfully")

        return presignedUrlResponse.objectKey
    }

    private fun getContentType(file: File): String {
        return when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "image/jpeg" // Default to JPEG
        }
    }
}