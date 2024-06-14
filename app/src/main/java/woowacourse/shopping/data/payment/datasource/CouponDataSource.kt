package woowacourse.shopping.data.payment.datasource

import woowacourse.shopping.data.payment.model.CouponData

interface CouponDataSource {
    suspend fun loadCoupons(): Result<List<CouponData>>
}
