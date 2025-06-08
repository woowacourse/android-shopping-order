package woowacourse.shopping.data.model

import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import java.time.LocalDateTime

data class CouponPolicyContext(
    val totalAmount: Long,
    val currentDateTime: LocalDateTime,
    val orderProducts: List<ProductUiModel>,
)
