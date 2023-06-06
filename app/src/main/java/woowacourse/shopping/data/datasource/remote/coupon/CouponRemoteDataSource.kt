package woowacourse.shopping.data.datasource.remote.coupon

import woowacourse.shopping.data.remote.request.CouponDTO
import woowacourse.shopping.data.remote.request.CouponDiscountPriceDTO

interface CouponRemoteDataSource {
    fun getCoupons(callback: (Result<List<CouponDTO>>) -> Unit)
    fun getPriceWithCoupon(originalPrice: Int, couponId: Long, callback: (Result<CouponDiscountPriceDTO>) -> Unit)
}
