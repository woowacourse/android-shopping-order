package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Coupon

interface OrderRepository {
    suspend fun orderShoppingCart(ids: List<Long>): Result<Unit>

    suspend fun getCoupons(): Result<List<Coupon>>
}
