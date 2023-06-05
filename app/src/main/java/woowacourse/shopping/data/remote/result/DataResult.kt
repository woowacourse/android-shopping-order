package woowacourse.shopping.data.remote.result

sealed class DataResult<out T> {
    class Success<out T> (val response: T) : DataResult<T>()
    class Failure(val message: String) : DataResult<Nothing>()
}
