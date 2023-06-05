package woowacourse.shopping.data.remote.request

import kotlinx.serialization.Serializable

@Serializable
data class CouponDiscountPriceDTO(
    val discountPrice: Int,
    val totalPrice: Int,
)
