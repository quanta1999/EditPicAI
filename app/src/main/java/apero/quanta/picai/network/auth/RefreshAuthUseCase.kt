package apero.quanta.picai.network.auth

import apero.quanta.picai.data.remote.dto.auth.AuthData
import apero.quanta.picai.data.remote.service.AuthApiService
import apero.quanta.picai.data.remote.service.RefreshTokenRequest
import apero.quanta.picai.data.remote.service.SilentLoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class RefreshAuthUseCase @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val deviceInfoProvider: DeviceInfoProvider,
) {

    suspend operator fun invoke(): Result<AuthData> = runCatching {
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken == null) {
            loginAndSaveToken().getOrThrow()
                ?: throw IllegalStateException("Login failed: AuthData is null")
        } else {
            val shouldRefresh = tokenManager.willAccessTokenExpireWithin(TimeUnit.HOURS.toMillis(1))
            if (shouldRefresh) {
                val refreshAuthData = refreshAndSaveToken(refreshToken).getOrNull()
                refreshAuthData ?: loginAndSaveToken().getOrThrow()
                ?: throw IllegalStateException("Login failed: AuthData is null")
            } else {
                // Token won't expire soon, return AuthData from stored tokens
                createAuthDataFromStoredTokens() ?: loginAndSaveToken().getOrThrow()
                ?: throw IllegalStateException("Login failed: AuthData is null")
            }

        }
    }

    private suspend fun loginAndSaveToken(): Result<AuthData?> = runCatching {
        withContext(Dispatchers.IO) {
            val deviceId = deviceInfoProvider.getDeviceId()
            val loginAuthData =
                authApiService.silentLogin(
                    "tera.aiartgenerator.aiphoto.aiphotoenhancer",
                    SilentLoginRequest(deviceId)
                ).getOrThrow()
            tokenManager.saveTokens(
                accessToken = loginAuthData.accessToken.orEmpty(),
                refreshToken = loginAuthData.refreshToken.orEmpty(),
                expiresIn = loginAuthData.expiresIn ?: 0,
                refreshExpiresIn = loginAuthData.refreshExpiresIn ?: 0,
                tokenType = loginAuthData.tokenType,
            )
            loginAuthData
        }
    }

    private suspend fun refreshAndSaveToken(refreshToken: String): Result<AuthData?> = runCatching {
        withContext(Dispatchers.IO) {
            val authData =
                authApiService.refreshToken(
                    "tera.aiartgenerator.aiphoto.aiphotoenhancer",
                    RefreshTokenRequest(refreshToken)
                ).getOrThrow()
            tokenManager.saveTokens(
                accessToken = authData.accessToken.orEmpty(),
                refreshToken = authData.refreshToken.orEmpty(),
                expiresIn = authData.expiresIn ?: 0,
                refreshExpiresIn = authData.refreshExpiresIn ?: 0,
                tokenType = authData.tokenType,
            )
            authData
        }
    }


    private fun createAuthDataFromStoredTokens(): AuthData? {
        val accessToken = tokenManager.getAccessToken()
        val refreshToken = tokenManager.getRefreshToken()

        if (accessToken == null || refreshToken == null) {
            return null
        }

        val accessTokenExpiry = tokenManager.getAccessTokenExpiry()
        val refreshTokenExpiry = tokenManager.getRefreshTokenExpiry()
        val currentTime = System.currentTimeMillis()

        if (accessTokenExpiry <= currentTime || refreshTokenExpiry <= currentTime) {
            return null
        }

        val expiresIn = ((accessTokenExpiry - currentTime) / 1000).toInt().coerceAtLeast(0)
        val refreshExpiresIn = ((refreshTokenExpiry - currentTime) / 1000).toInt().coerceAtLeast(0)

        val tokenType = tokenManager.getTokenType()

        return AuthData(
            tokenType = tokenType,
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = expiresIn,
            refreshExpiresIn = refreshExpiresIn,
        )
    }
}