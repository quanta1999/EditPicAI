package apero.quanta.picai.network

import apero.quanta.picai.network.auth.DeviceInfoProvider
import com.apero.signature.SignatureParser
import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response
import apero.quanta.picai.BuildConfig


class SignatureInterceptor @Inject constructor(
    private val deviceInfoProvider: DeviceInfoProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        addSignatureHeaders(requestBuilder)
        return chain.proceed(requestBuilder.build())
    }

    private fun addSignatureHeaders(requestBuilder: okhttp3.Request.Builder) {
        val currentTimeMillis = System.currentTimeMillis()
        val signatureData = SignatureParser.parseData(
            keyId = BuildConfig.BUNDLE_ID,
            publicKeyStr = BuildConfig.PUBLIC_KEY,
            timeStampParse = currentTimeMillis,
        )

        requestBuilder.apply {
            addHeader(HEADER_API_APP_VERSION, deviceInfoProvider.getAppVersion())
            addHeader(HEADER_API_COUNTRY, deviceInfoProvider.getCountryCode())
            addHeader(HEADER_API_COUNTRY_CODE, "abc")
            addHeader(HEADER_API_SIGNATURE, signatureData.signature)
            addHeader(HEADER_API_TIMESTAMP, signatureData.timeStamp.toString())
            addHeader(HEADER_BUNDLE_ID, BuildConfig.BUNDLE_ID)
            addHeader(HEADER_APP_NAME, BuildConfig.HEADER_APP_NAME)
            addHeader(HEADER_API_TOKEN, DEFAULT_API_TOKEN)
        }
    }

    private companion object {
        const val HEADER_API_APP_VERSION = "app-version"
        const val HEADER_API_COUNTRY = "country-code"
        const val HEADER_API_COUNTRY_CODE = "x-api-country-code"
        const val HEADER_API_SIGNATURE = "X-Api-Signature"
        const val HEADER_API_TIMESTAMP = "X-Api-Timestamp"
        const val HEADER_BUNDLE_ID = "X-Api-BundleId"
        const val HEADER_API_TOKEN = "x-api-token"
        const val HEADER_APP_NAME = "App-name"

        const val DEFAULT_API_TOKEN = "not_get_api_token"
    }

}