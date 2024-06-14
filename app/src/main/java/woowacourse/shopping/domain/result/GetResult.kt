package woowacourse.shopping.domain.result

fun <T, E : RootError> Result<T, E>.getOrThrow(): T =
    when (this) {
        is Result.Success -> data
        is Result.Error -> throw RuntimeException("$error")
    }

fun <T, E : RootError> Result<T, E>.getOrNull(): T? =
    when (this) {
        is Result.Success -> data
        is Result.Error -> null
    }
