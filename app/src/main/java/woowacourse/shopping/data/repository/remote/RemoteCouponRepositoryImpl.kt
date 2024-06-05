package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.CouponDataSourceImpl
import woowacourse.shopping.data.source.CouponDataSource
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class RemoteCouponRepositoryImpl(
    private val couponDataSource: CouponDataSource = CouponDataSourceImpl(),
) : CouponRepository {
    override suspend fun loadCoupons(): Result<List<Coupon>> {
        return couponDataSource.loadCoupons()
    }

    override fun loadDeliveryCharge(): Int {
        return DELIVERY_CHARGE
    }

    companion object {
        private const val DELIVERY_CHARGE = 3000
    }
}
