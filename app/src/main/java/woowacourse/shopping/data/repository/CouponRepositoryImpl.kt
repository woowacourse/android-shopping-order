package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.dto.order.toDomain
import woowacourse.shopping.order.Coupon

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): List<Coupon> =
        try {
            couponDataSource.fetchCoupons().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
}
