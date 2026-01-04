package apero.quanta.picai.network.exeption

import java.io.IOException


sealed class RemoteException(message: String = "") : IOException(message) {

    open class HttpException(
        val errorCode: Int,
        message: String = "HTTP Error $errorCode"
    ) : RemoteException(message) {
        class AuthenticateException(message: String = "Authentication failed") :
            HttpException(401, message)

        class InternalServerErrorException(message: String = "Internal server error") :
            HttpException(500, message)
    }

    class NoInternetException(message: String = "No internet connection") :
        RemoteException(message)

    class UnableAccessServerException(message: String = "Unable to access server") :
        RemoteException(message)

    class UnknownServerException(message: String = "Unknown server error") :
        RemoteException(message)

    class SocketTimeoutException(message: String = "Request timeout") : RemoteException(message)

    class ApiKeyNotAvailableException(message: String = "API key not available") :
        RemoteException(message)

    class NSFWException(message: String = "NSFW content detected") : RemoteException(message)
}