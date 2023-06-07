package woowacourse.shopping.data.remote.result

sealed class DataResult<out T> {
    class Success<out T> (val response: T) : DataResult<T>()
    object Failure : DataResult<Nothing>()
    object NotSuccessfulError : DataResult<Nothing>()
    object WrongResponse : DataResult<Nothing>()
}
