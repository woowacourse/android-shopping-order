package woowacourse.shopping.data.remote.coupon

class CouponRepository(
    private val couponService: CouponService,
) {
    suspend fun fetchAllCoupons(): List<CouponResponse> = couponService.requestCoupons()
}
