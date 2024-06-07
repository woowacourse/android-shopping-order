package woowacourse.shopping.data.coupon

interface CouponRepository {
    suspend fun getCoupons(): Result<List<CouponState>>
}
