package woowacourse.shopping.data.datasource

import retrofit2.Response

fun <T> Response<T>.getResult(errorMessage: String): Result<T> {

    return this.body()?.run {
        Result.success(this)
    } ?: Result.failure(Throwable(errorMessage))
}

fun Response<Unit>.getResultOnHeaders(errorMessage: String): Result<Long> {

    return this.headers()[LOCATION]?.run {
        Result.success(split("/").last().toLong())
    } ?: Result.failure(Throwable(errorMessage))
}

private const val LOCATION = "Location"
