package woowacourse.shopping.data.util

import retrofit2.Response

fun <T, R> handleResponse(
    response: Response<T>,
    data: R,
): Result<R> {
    return if (response.isSuccessful) {
        Result.success(data)
    } else {
        Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
    }
}
