package woowacourse.shopping.data.util

import retrofit2.HttpException
import retrofit2.Response

fun <T> Response<T>.requireBody(): T {
    if (isSuccessful) {
        return body() ?: throw IllegalStateException("Response body is null")
    } else {
        throw HttpException(this)
    }
}

fun Response<*>.requireHeader(name: String): String {
    if (isSuccessful) {
        return headers()[name] ?: throw IllegalArgumentException("Missing required header: $name")
    } else {
        throw HttpException(this)
    }
}
