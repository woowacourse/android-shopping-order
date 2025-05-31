package woowacourse.shopping.data.datasource

fun <T> handleFailure(onResult: (Result<T>) -> Unit) {
    onResult(Result.failure(Exception("응답에 실패했습니다.")))
}
