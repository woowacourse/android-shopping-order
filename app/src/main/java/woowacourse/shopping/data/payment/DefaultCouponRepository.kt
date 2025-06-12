package woowacourse.shopping.data.payment

import woowacourse.shopping.domain.payment.Coupon
import woowacourse.shopping.domain.payment.CouponRepository

class DefaultCouponRepository(
    private val remoteDataSource: RemoteCouponDataSource = RemoteCouponDataSource(),
) : CouponRepository {
    override suspend fun loadCoupons(): Result<List<Coupon>> =
        runCatching {
            val coupons: List<CouponDataModel> = remoteDataSource.loadCoupons()
            coupons.mapNotNull(CouponDataModel::toDomain)
        }
}
