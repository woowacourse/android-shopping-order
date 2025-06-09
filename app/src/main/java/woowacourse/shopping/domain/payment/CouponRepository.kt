package woowacourse.shopping.domain.payment

interface CouponRepository {
    suspend fun loadCoupons(): Result<List<Coupon>>
}
