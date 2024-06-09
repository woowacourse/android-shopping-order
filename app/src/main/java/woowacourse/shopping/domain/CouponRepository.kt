package woowacourse.shopping.domain

interface CouponRepository {
    suspend fun getCoupons(): Result<List<Coupon>>
}
