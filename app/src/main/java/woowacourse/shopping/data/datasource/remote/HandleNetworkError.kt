package woowacourse.shopping.data.datasource.remote

inline fun <T> handleApiCall(
    errorMessage: String,
    action: () -> T,
): Result<T> =
    runCatching { action() }
        .onFailure { e ->
            e.printStackTrace()
            throw Exception("$errorMessage\n인터넷 연결을 확인해주세요.")
        }
