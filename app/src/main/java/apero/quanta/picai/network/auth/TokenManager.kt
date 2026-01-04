package apero.quanta.picai.network.auth

import android.content.Context
import apero.quanta.picai.data.local.secure.SecureDataStore

class TokenManager(context: Context) {

    private val secureDataStore = SecureDataStore.getInstance(context, PREFS_NAME)

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresIn: Int,
        refreshExpiresIn: Int,
        tokenType: String?,
    ) {
        val currentTime = System.currentTimeMillis()
        secureDataStore.putString(KEY_ACCESS_TOKEN, accessToken)
        secureDataStore.putString(KEY_REFRESH_TOKEN, refreshToken)
        secureDataStore.putString(KEY_TOKEN_TYPE, tokenType.orEmpty())
        secureDataStore.putLong(KEY_ACCESS_TOKEN_EXPIRY, currentTime + (expiresIn * 1000L))
        secureDataStore.putLong(KEY_REFRESH_TOKEN_EXPIRY, currentTime + (refreshExpiresIn * 1000L))
    }


    fun getAccessToken(): String? {
        val token = secureDataStore.getStringBlocking(KEY_ACCESS_TOKEN, "")
        return token.ifEmpty { null }
    }


    fun getRefreshToken(): String? {
        val token = secureDataStore.getStringBlocking(KEY_REFRESH_TOKEN, "")
        return token.ifEmpty { null }
    }


    fun willAccessTokenExpireWithin(milliseconds: Long): Boolean {
        val expiry = secureDataStore.getLongBlocking(KEY_ACCESS_TOKEN_EXPIRY, 0L)
        if (expiry == 0L) return true // No expiry set, treat as expired
        val currentTime = System.currentTimeMillis()
        val timeUntilExpiry = expiry - currentTime
        return timeUntilExpiry <= milliseconds
    }


    fun getAccessTokenExpiry(): Long {
        return secureDataStore.getLongBlocking(KEY_ACCESS_TOKEN_EXPIRY, 0L)
    }


    fun getTokenType(): String {
        val tokenType = secureDataStore.getStringBlocking(KEY_TOKEN_TYPE, "")
        return tokenType.ifEmpty { DEFAULT_TOKEN_TYPE }
    }


    fun getRefreshTokenExpiry(): Long {
        return secureDataStore.getLongBlocking(KEY_REFRESH_TOKEN_EXPIRY, 0L)
    }


    fun clearTokens() {
        secureDataStore.clearBlocking()
    }

    companion object {
        private const val PREFS_NAME = "metart_auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_ACCESS_TOKEN_EXPIRY = "access_token_expiry"
        private const val KEY_REFRESH_TOKEN_EXPIRY = "refresh_token_expiry"
        private const val DEFAULT_TOKEN_TYPE = "Bearer"
    }
}
