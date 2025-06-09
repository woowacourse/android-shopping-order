package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.CouponApi
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepository(
    private val api: CouponApi,
    private var cachedCoupons: List<Coupon> = emptyList(),
) : CouponRepository {
    override suspend fun fetchAllCoupons(): Result<List<Coupon>> =
        runCatching {
            val response = api.getAllCoupons()
            if (response.isSuccessful) {
                val allCoupons = response.body()?.map { it.toDomain() } ?: emptyList()
                cachedCoupons = allCoupons
                allCoupons
            } else {
                throw IllegalArgumentException()
            }
        }

    override fun fetchCoupon(couponId: Long): Coupon? = cachedCoupons.find { it.id == couponId }
}
