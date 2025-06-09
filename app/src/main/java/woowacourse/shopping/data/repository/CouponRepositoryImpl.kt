package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    private var _cachedCoupons: List<Coupon>? = null
    override val cachedCoupon: List<Coupon>?
        get() = _cachedCoupons

    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            _cachedCoupons ?: fetchAndCacheCoupons()
        }

    override suspend fun getAvailableCoupons(cartItems: List<CartItem>): Result<List<Coupon>> =
        runCatching {
            val allCoupons = getCoupons().getOrThrow()
            allCoupons.filter { it.isAvailable(cartItems) }
        }

    private suspend fun fetchAndCacheCoupons(): List<Coupon> {
        val fetched = couponDataSource.fetchCoupons().getOrThrow().map { it.toDomain() }
        _cachedCoupons = fetched
        return fetched
    }
}
