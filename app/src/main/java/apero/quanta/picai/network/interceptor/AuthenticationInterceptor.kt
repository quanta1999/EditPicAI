package apero.quanta.picai.network.interceptor

import apero.quanta.picai.network.auth.RefreshAuthUseCase
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.net.HttpHeaders
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock

class AuthenticationInterceptor @Inject constructor(
    private val refreshAuthUseCase: RefreshAuthUseCase,
) : Interceptor {

    private val refreshLock = ReentrantLock()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Check if token will expire within threshold and refresh proactively if needed
        val accessToken = getOrCreateNewAccessTokenIfNeeded()

        // Add Authorization header to the request if access token exists
        val requestWithAuth =
            if (accessToken != null && !NetworkConstant.NOT_ADD_HEADER_METHODS.contains(originalRequest.method)) {
                originalRequest.newBuilder()
                    .header(HttpHeaders.AUTHORIZATION, createAuthHeader(accessToken))
                    .build()
            } else {
                originalRequest
            }

        // Proceed with the request (with or without auth header)
        val response = chain.proceed(requestWithAuth)

        // Check if we received a 401 Unauthorized response
        if (response.code == NetworkConstant.HTTP_UNAUTHORIZED_ERROR) {
            Timber.d("Received 401 Unauthorized, attempting to refresh token")

            // Attempt to refresh the token
            val newAccessToken = getOrCreateNewAccessTokenIfNeeded()

            if (newAccessToken != null) {
                Timber.d("Token refresh successful, retrying original request")

                // Close the original response since we're retrying
                response.close()

                // Retry the original request with the new token
                val newRequest = originalRequest.newBuilder()
                    .header(HttpHeaders.AUTHORIZATION, createAuthHeader(newAccessToken))
                    .build()

                return chain.proceed(newRequest)
            } else {
                Timber.e("Token refresh failed, returning original 401 response")
                // Return the original response with its body intact
                return response
            }
        }

        return response
    }


    private fun getOrCreateNewAccessTokenIfNeeded(): String? = refreshLock.withLock {
        Timber.d("Acquiring lock for token refresh")

        // Call the refresh use case
        return@withLock runBlocking {
            try {
                val result = refreshAuthUseCase()
                result.fold(
                    onSuccess = { authData ->
                        Timber.d("Token refresh successful")
                        authData.accessToken
                    },
                    onFailure = { error ->
                        Timber.e(error, "Token refresh failed: ${error.message}")
                        null
                    },
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception during token refresh")
                null
            }
        }
    }

    private fun createAuthHeader(accessToken: String): String {
        return "Bearer $accessToken"
    }
}

object NetworkConstant {
    val NOT_ADD_HEADER_METHODS = listOf("PUT")
    const val HTTP_UNAUTHORIZED_ERROR = 401
}