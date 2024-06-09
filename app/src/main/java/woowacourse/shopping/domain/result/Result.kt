package woowacourse.shopping.domain.result

sealed interface Result<out T : Any?> {
    data class Success<T : Any?>(val result: T) :
        Result<T>

    data class Exception<T : Any?>(val e: Throwable) :
        Result<T>
}

sealed interface Fail<T : Any?> : Result<T> {
    val message: String?

    data class NotFound<T : Any?>(override val message: String?) : Fail<T>

    data class Network<T : Any?>(override val message: String?) : Fail<T>

    data class InvalidAuthorized<T : Any?>(override val message: String?) : Fail<T>
}
