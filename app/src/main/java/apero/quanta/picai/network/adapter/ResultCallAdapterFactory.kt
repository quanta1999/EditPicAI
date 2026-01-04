package apero.quanta.picai.network.adapter

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


class ResultCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)

        // Only handle Call<Result<T>>
        if (rawType != Call::class.java) return null

        val callType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(callType) != Result::class.java) return null

        // Inner type T of Result<T>
        val successType = getParameterUpperBound(0, callType as ParameterizedType)

        return ResultCallAdapter<Any>(successType)
    }

    private class ResultCallAdapter<R>(
        private val responseType: Type,
    ) : CallAdapter<R, Call<Result<R>>> {

        override fun responseType(): Type {
            // For Unit type (empty responses), don't wrap in ApiResponse
            if (responseType == Unit::class.java || responseType == Void::class.java) {
                return responseType
            }

            // Tell Retrofit to deserialize to ApiResponse<T> for JSON responses
            // This way Gson will properly deserialize the full ApiResponse structure
            return object : ParameterizedType {
                override fun getRawType() = ApiResponse::class.java
                override fun getOwnerType() = null
                override fun getActualTypeArguments() = arrayOf(responseType)
            }
        }

        override fun adapt(call: Call<R>): Call<Result<R>> {
            return ResultCall(call, responseType)
        }
    }

    private class ResultCall<R>(
        private val delegate: Call<R>,
        private val responseType: Type
    ) : Call<Result<R>> {

        override fun enqueue(callback: Callback<Result<R>>) {
            delegate.enqueue(
                object : Callback<R> {
                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        val result = parseToResult(response)
                        callback.onResponse(this@ResultCall, Response.success(result))
                    }

                    override fun onFailure(call: Call<R>, t: Throwable) {
                        callback.onResponse(this@ResultCall, Response.success(Result.failure(t)))
                    }
                },
            )
        }

        override fun execute(): Response<Result<R>> {
            val response = delegate.execute()
            val result = parseToResult(response)
            return Response.success(result)
        }

        override fun clone(): Call<Result<R>> = ResultCall(delegate.clone(), responseType)
        override fun cancel() = delegate.cancel()
        override fun isExecuted() = delegate.isExecuted
        override fun isCanceled() = delegate.isCanceled
        override fun request() = delegate.request()
        override fun timeout() = delegate.timeout()

        @Suppress("UNCHECKED_CAST")
        private fun <R> parseToResult(response: Response<R>): Result<R> =
            runCatching {
                return@runCatching if (response.isSuccessful) {
                    // Handle Unit/Void responses (empty body)
                    if (responseType == Unit::class.java || responseType == Void::class.java) {
                        Unit as R
                    } else {
                        val body = response.body()
                        when {
                            // Handle ApiResponse wrapper - extract data field
                            body is ApiResponse<*> -> {
                                body.data as R
                            }
                            // Handle direct response
                            else -> {
                                body as R
                            }
                        }
                    }
                } else {
                    throw Exception("HTTP ${response.code()}: ${response.message()}")
                }
            }
    }
}

open class ApiResponse<T>(
    @SerializedName("data")
    val data: T? = null,
)