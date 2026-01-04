package apero.quanta.picai.data.remote.service

import apero.quanta.picai.data.remote.dto.auth.AuthData
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    /**
     * Performs silent login to obtain access token
     * The token is used for subsequent API calls to Strapi endpoints
     */
    @POST("/saas-user-service/v1/users/silent-login")
    suspend fun silentLogin(
        @Header("x-api-bundleId") bundleId: String,
        @Body request: SilentLoginRequest,
    ): Result<AuthData>

    @POST("/saas-user-service/v1/users/generate-new-token")
    suspend fun refreshToken(
        @Header("x-api-bundleId") bundleId: String,
        @Body request: RefreshTokenRequest,
    ): Result<AuthData>
}

data class SilentLoginRequest(
    @SerializedName("deviceId")
    val deviceId: String
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)