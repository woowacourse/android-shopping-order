package woowacourse.shopping.data.repository.real

import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.source.CouponDataSourceImpl
import woowacourse.shopping.data.source.CouponDataSource
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource = CouponDataSourceImpl(NetworkManager.getApiClient()),
) : CouponRepository {
    override suspend fun loadCoupons(): Result<List<Coupon>> {
        return couponDataSource.loadCoupons()
    }

    companion object {
        private const val DELIVERY_CHARGE = 3000
    }
}
