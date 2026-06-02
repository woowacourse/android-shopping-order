package woowacourse.shopping.error

typealias rootError = Error

sealed interface Result<out D, out E : rootError> {
    data class Success<out D, out E : rootError>(
        val data: D,
    ) : Result<D, E>

    data class Error<out D, out E : rootError>(
        val error: E,
    ) : Result<D, E>
}
