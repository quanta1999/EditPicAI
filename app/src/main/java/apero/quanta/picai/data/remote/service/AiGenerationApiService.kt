package apero.quanta.picai.data.remote.service

import apero.quanta.picai.data.remote.dto.category.CategoryListResponse
import apero.quanta.picai.data.remote.dto.genimg.ImageGenerationRequest
import apero.quanta.picai.data.remote.dto.category.StyleListResponse
import apero.quanta.picai.data.remote.dto.genimg.GeneratedImageData
import apero.quanta.picai.data.remote.dto.genimg.PresignedUrlData
import apero.quanta.picai.data.remote.dto.genimg.PresignedUrlRequest
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface AiGenerationApiService {
    @GET("/api/v1/strapi/categories")
    suspend fun getCategories(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("aiType") aiType: String? = null,
        @Query("display") display: String? = null
    ): Result<CategoryListResponse>


    @GET("/api/v1/strapi/categories/{categoryId}/templates")
    suspend fun getCategoryTemplates(
        @Path("categoryId") categoryId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
    ): Result<StyleListResponse>

    //    Gen Image

    @PUT
    suspend fun uploadToS3(
        @Url url: String,
        @Body body: RequestBody,
    ): Result<Unit>

    @POST("/api/ai-generation/image")
    suspend fun generateImage(
        @Body request: ImageGenerationRequest,
    ): Result<GeneratedImageData>

    @POST("/api/ai-generation/presigned-url")
    suspend fun getPresignedUrl(
        @Body request: PresignedUrlRequest,
    ): Result<PresignedUrlData>
}