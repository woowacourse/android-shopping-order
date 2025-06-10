package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    private var cachedCoupons: List<Coupon>? = null

    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            cachedCoupons ?: fetchAndCacheCoupons()
        }

    override suspend fun getAvailableCoupons(cartItems: List<CartItem>): Result<List<Coupon>> =
        runCatching {
            val allCoupons = getCoupons().getOrThrow()
            allCoupons.filter { it.isAvailable(cartItems) }
        }

    override fun getCouponById(couponId: Long): Result<Coupon?> =
        runCatching {
            cachedCoupons?.firstOrNull { it.id == couponId }
        }

    private suspend fun fetchAndCacheCoupons(): List<Coupon> {
        val fetched = couponDataSource.fetchCoupons().getOrThrow().map { it.toDomain() }
        cachedCoupons = fetched
        return fetched
    }
}
