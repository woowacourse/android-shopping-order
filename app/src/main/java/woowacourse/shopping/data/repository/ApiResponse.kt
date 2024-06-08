package woowacourse.shopping.data.repository

sealed interface ApiResponse<out T : Any?> {
    data class Success<T : Any?>(val data: T) : ApiResponse<T>

    data class Exception<T : Any?>(val e: Throwable) : ApiResponse<T>
}

sealed interface Error<T : Any?> : ApiResponse<T> {
    val message: String?

    data class NotFound<T : Any?>(override val message: String?) : Error<T>

    data class Network<T : Any?>(override val message: String?) : Error<T>

    data class Unauthorized<T : Any?>(override val message: String?) : Error<T>

    data class Unknown<T : Any?>(override val message: String?) : Error<T>
}
