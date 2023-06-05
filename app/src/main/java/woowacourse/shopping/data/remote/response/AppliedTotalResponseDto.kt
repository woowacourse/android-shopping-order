package woowacourse.shopping.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class AppliedTotalResponseDto(
    val discountPrice: Int,
    val totalPrice: Int,
)
