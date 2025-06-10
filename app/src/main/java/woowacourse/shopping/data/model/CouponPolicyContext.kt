package woowacourse.shopping.data.model

import woowacourse.shopping.domain.model.Product
import java.time.LocalDateTime

data class CouponPolicyContext(
    val totalAmount: Long,
    val currentDateTime: LocalDateTime,
    val orderProducts: List<Product>,
)
