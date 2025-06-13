package woowacourse.shopping.data.coupons.repository

import woowacourse.shopping.domain.model.Coupon

interface OrderRepository {
    suspend fun fetchCoupons(): List<Coupon>
    suspend fun addOrder(cartItemIds: List<Int>)
}
