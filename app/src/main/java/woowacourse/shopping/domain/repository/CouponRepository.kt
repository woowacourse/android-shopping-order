package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

interface CouponRepository {
    suspend fun getCoupons(
        totalAmount: Long,
        orderProducts: List<ProductUiModel>,
    ): Result<List<Coupon>>
}
