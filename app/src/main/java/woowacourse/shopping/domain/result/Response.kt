package woowacourse.shopping.domain.result

sealed interface Response<out T : Any?> {
    data class Success<T : Any?>(val result: T) : Response<T>

    data class Exception<T : Any?>(val e: Throwable) : Response<T>
}

sealed interface Fail<T : Any?> : Response<T> {
    val message: String?

    data class NotFound<T : Any?>(override val message: String?) : Fail<T>

    data class Network<T : Any?>(override val message: String?) : Fail<T>

    data class InvalidAuthorized<T : Any?>(override val message: String?) : Fail<T>
}


