package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.coupon.CouponRemoteDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val dataSource: CouponRemoteDataSource,
    private var cachedCoupons: List<Coupon> = emptyList(),
) : CouponRepository {
    override suspend fun fetchAllCoupons(): Result<List<Coupon>> =
        runCatching {
            val response = dataSource.fetchAllCoupons()
            val allCoupons = response.map { it.toDomain() }
            cachedCoupons = allCoupons

            allCoupons
        }

    override fun fetchCoupon(couponId: Long): Coupon? = cachedCoupons.find { it.id == couponId }
}
