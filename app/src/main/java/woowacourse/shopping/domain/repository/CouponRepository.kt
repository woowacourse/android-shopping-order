package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.payment.model.CouponData

interface CouponRepository {
    suspend fun loadCoupons(): Result<List<CouponData>>
}
