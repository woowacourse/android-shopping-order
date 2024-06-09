package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.RemoteCouponDataSource
import woowacourse.shopping.data.model.coupon.CouponResponseItem
import woowacourse.shopping.data.model.coupon.toCoupon
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponRemoteDataSource: RemoteCouponDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> {
        return runCatching {
            couponRemoteDataSource.getCoupons().map(CouponResponseItem::toCoupon)
        }
    }
}
