package woowacourse.shopping.data.payment.datasource

import woowacourse.shopping.data.payment.model.CouponsData

interface CouponDataSource {
    suspend fun loadCoupons(): Result<CouponsData>
}
