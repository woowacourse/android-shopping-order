package woowacourse.shopping.data.payment.remote.dto

import woowacourse.shopping.data.payment.remote.dto.CouponDto

data class CouponResponse(
    val coupons: List<CouponDto>,
)