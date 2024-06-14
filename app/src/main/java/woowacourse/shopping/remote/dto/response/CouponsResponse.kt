package woowacourse.shopping.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CouponsResponse(
    val coupons: List<CouponResponse>,
)
