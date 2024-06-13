package woowacourse.shopping.domain.result

inline fun <T : Any?, E : Error> Result<T, E>.onSuccess(executable: (T) -> Unit): Result<T, E> =
    apply {
        if (this is Result.Success) {
            executable(data)
        }
    }

inline fun <T : Any?, E : Error> Result<T, E>.onError(executable: (E) -> Unit): Result<T, E> =
    apply {
        if (this is Result.Error) {
            executable(error)
        }
    }
