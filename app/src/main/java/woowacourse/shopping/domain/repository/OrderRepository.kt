package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.coupon.Coupon

interface OrderRepository {
    suspend fun fetchCouponList(): Result<List<Coupon>>

    suspend fun makeOrder(paymentAmount: Int): Result<Unit>
}
