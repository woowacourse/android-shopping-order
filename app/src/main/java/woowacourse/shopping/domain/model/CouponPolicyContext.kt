package woowacourse.shopping.domain.model

import java.time.LocalDateTime

data class CouponPolicyContext(
    val totalAmount: Long,
    val currentDateTime: LocalDateTime,
    val orderProducts: List<Product>,
)
