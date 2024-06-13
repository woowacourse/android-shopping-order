package woowacourse.shopping.domain.result

sealed interface DataError : Error {

    data object UNKNOWN : DataError, ShowError

    data object NotFound : DataError

    enum class Network : DataError, ShowError {
        REQUEST_TIMEOUT,
        NO_INTERNET,
        SERVER,
        INVALID_AUTHORIZATION
    }

    enum class Local : DataError {
        INVALID_ID,
    }
}

inline fun <T, E : DataError, S> Result<T, E>.transForm(executable: (T) -> S): Result<S, E> =
    when (this) {
        is Result.Success -> Result.Success(executable(data))
        is Result.Error -> Result.Error(this.error)
    }


