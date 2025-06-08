package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CouponDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            couponDataSource.fetchCoupons().getOrThrow().map { it.toDomain() }
        }

    override suspend fun getAvailableCoupons(cartItems: List<CartItem>): Result<List<Coupon>> =
        runCatching {
            val allCoupons = couponDataSource.fetchCoupons().getOrThrow().map { it.toDomain() }
            allCoupons.filter { it.isAvailable(cartItems) }
        }
}
