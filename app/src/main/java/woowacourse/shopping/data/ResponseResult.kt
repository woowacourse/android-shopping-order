package woowacourse.shopping.data

sealed interface ResponseResult<T : Any> {
    class Success<T : Any>(val data: T) : ResponseResult<T>

    class Error<T : Any>(val code: Int, val message: String?) : ResponseResult<T>

    class Exception<T : Any>(val e: Throwable) : ResponseResult<T>
}
