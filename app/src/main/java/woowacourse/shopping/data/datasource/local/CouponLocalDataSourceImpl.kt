package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.domain.model.coupon.Coupon

class CouponLocalDataSourceImpl : CouponLocalDataSource {
    private val couponMap = mutableMapOf<Long, Coupon>()

    override suspend fun saveCoupons(coupons: List<Coupon>) {
        couponMap.putAll(coupons.associateBy { it.id })
    }

    override suspend fun getCoupons(): List<Coupon> = couponMap.values.toList()

    override suspend fun getCouponById(id: Long): Coupon? = couponMap[id]
}
