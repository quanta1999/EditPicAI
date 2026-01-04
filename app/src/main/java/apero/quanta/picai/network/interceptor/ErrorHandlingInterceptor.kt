package apero.quanta.picai.network.interceptor

import apero.quanta.picai.network.exeption.RemoteException
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class ErrorHandlingInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return try {
            val response = chain.proceed(request)

            // Check for HTTP error codes
            if (!response.isSuccessful) {
                val errorBody = response.body.string()
                val errorMessage = extractErrorMessage(errorBody)
                val errorCode = extractErrorCode(errorBody)
                throw mapHttpErrorToException(response.code, errorMessage.ifBlank { response.message }, errorCode)
            }

            response
        } catch (e: Exception) {
            throw mapExceptionToDomain(e)
        }
    }


    private fun extractErrorMessage(errorBody: String): String {
        return try {
            val json = JSONObject(errorBody)
            json.optString("message", errorBody)
        } catch (e: Exception) {
            errorBody
        }
    }


    private fun extractErrorCode(errorBody: String): String {
        return try {
            val json = JSONObject(errorBody)
            json.optString("errorCode", errorBody)
        } catch (e: Exception) {
            errorBody
        }
    }


    private fun mapHttpErrorToException(code: Int, message: String, errorCode: String): RemoteException {
        if (errorCode == NSFW_ERROR_CODE) {
            return RemoteException.NSFWException(message = message)
        }
        return when (code) {
            401 -> RemoteException.HttpException.AuthenticateException(
                message = message.ifEmpty { "Authentication failed" },
            )

            in HTTP_INTERNAL_ERROR..HTTP_NETWORK_CONNECT_TIMEOUT -> {
                RemoteException.HttpException.InternalServerErrorException(
                    message = message.ifEmpty { "Internal server error" },
                )
            }

            else -> RemoteException.HttpException(
                errorCode = code,
                message = message.ifEmpty { "HTTP Error $code" },
            )
        }
    }


    private fun mapExceptionToDomain(exception: Exception): RemoteException {
        return when (exception) {
            is RemoteException -> exception

            is UnknownHostException -> RemoteException.NoInternetException(
                message = "No internet connection available",
            )

            is SocketTimeoutException -> RemoteException.SocketTimeoutException(
                message = "Request timeout: ${exception.message ?: "Connection timed out"}",
            )

            is java.net.ConnectException -> RemoteException.UnableAccessServerException(
                message = "Unable to connect to server: ${exception.message ?: "Connection refused"}",
            )

            else -> RemoteException.UnknownServerException(
                message = "Network error: ${exception.message ?: exception::class.simpleName}",
            )
        }
    }

    private companion object {
        const val HTTP_INTERNAL_ERROR = 500
        const val HTTP_NETWORK_CONNECT_TIMEOUT = 599

        const val NSFW_ERROR_CODE = "ERR120"
    }
}