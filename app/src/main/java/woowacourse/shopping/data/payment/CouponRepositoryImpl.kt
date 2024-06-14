package woowacourse.shopping.data.payment

import woowacourse.shopping.data.payment.datasource.CouponDataSource
import woowacourse.shopping.data.payment.model.CouponData
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun loadCoupons(): Result<List<CouponData>> {
        return couponDataSource.loadCoupons()
    }
}
