package woowacourse.shopping.data.datasource.remote.coupon

import woowacourse.shopping.data.remote.request.CouponDTO
import woowacourse.shopping.data.remote.request.CouponDiscountPriceDTO

interface CouponDataSource {
    fun getCoupons(): Result<List<CouponDTO>>
    fun getPriceWithCoupon(originalPrice: Int, couponId: Long): Result<CouponDiscountPriceDTO>
}
