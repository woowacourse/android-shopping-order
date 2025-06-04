package woowacourse.shopping.data.datasource.remote

inline fun <T> handleApiCall(
    errorMessage: String,
    action: () -> T,
): Result<T> =
    runCatching { action() }
        .recoverCatching {
            throw Exception("$errorMessage\n인터넷 연결을 확인해주세요.")
        }
