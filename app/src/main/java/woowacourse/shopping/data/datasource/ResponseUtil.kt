package woowacourse.shopping.data.datasource

import retrofit2.Response

fun <T> Response<T>.getResult(errorMessage: String): Result<T> {

    return this.body()?.run {
        Result.success(this)
    } ?: Result.failure(Throwable(errorMessage))
}
