package woowacourse.shopping.data.remote.coupon

import woowacourse.shopping.data.remote.NetworkClient

class CouponRepository {
    suspend fun fetchAllCoupons(): CouponResponse = NetworkClient.getCouponService().requestCoupons()
}
