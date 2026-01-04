package apero.quanta.picai.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("data")
    val data: AuthData? = null,
)

data class AuthData(
    @SerializedName("tokenType")
    val tokenType: String? = null,

    @SerializedName("accessToken")
    val accessToken: String? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("expiresIn")
    val expiresIn: Int? = null,

    @SerializedName("refreshExpiresIn")
    val refreshExpiresIn: Int? = null,
)