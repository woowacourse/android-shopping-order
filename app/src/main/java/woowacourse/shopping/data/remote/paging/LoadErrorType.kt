package woowacourse.shopping.data.remote.paging

enum class LoadErrorType(val message: String, val code: Int) {
    UNKNOWN("UNKNOWN", 0),
    EMPTY_BODY("EMPTY", 1),

}
