package woowacourse.shopping.data.common.model

data class BaseResponse<T>(
    val message: String,
    val result: T?,
)
