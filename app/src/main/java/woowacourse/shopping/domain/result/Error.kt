package woowacourse.shopping.domain.result


sealed interface Error

inline fun <T, E : Error, S> Result<T, E>.transForm(executable: (T) -> S): Result<S, E> =
    when (this) {
        is Result.Success -> Result.Success(executable(data))
        is Result.Error -> Result.Error(this.error)
    }
