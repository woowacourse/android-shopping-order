package woowacourse.shopping.data.calladapter

import java.io.IOException
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter

class ApiResultCallAdapter<T>(
    private val responseType: Type,
) : CallAdapter<T, Any> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Any =
        suspend {
            try {
                val response = call.execute()
                val code = response.code()
                val body = response.body()
                val message = response.message()

                when {
                    response.isSuccessful && body != null -> ApiResult.Success(body)
                    code in 400..499 -> ApiResult.ClientError(code, message)
                    code in 500..599 -> ApiResult.ServerError(code, message)
                    else -> ApiResult.ApiError(code, message)
                }
            } catch (e: IOException) {
                ApiResult.NetworkError
            } catch (e: Exception) {
                ApiResult.UnknownError(e)
            }
        }
}
