package woowacourse.shopping.data.common

data class BaseResponse<T>(
    val message: String,
    val result: T?,
)
